package cn.chitanda.app.imovie.feature.setting

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.chitanda.app.imovie.core.data.repository.AppVersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:55
 * @description:
 **/
@HiltViewModel
class SettingScreenViewModel @Inject constructor(
    private val appVersionRepository: AppVersionRepository,
    private val context: Application,
) : ViewModel() {
    private val _settingUiState: MutableStateFlow<SettingUiState> =
        MutableStateFlow(SettingUiState.Loading)
    val settingUiState: StateFlow<SettingUiState> get() = _settingUiState

    init {
        viewModelScope.launch {
            flow<SettingUiState> {
                val version = getAppCurrentVersion()
                if (version != null) {
                    emit(SettingUiState.Success(SettingUiState.AppVersion.Newest("v$version")))
                    val release = appVersionRepository.checkAppNeedUpdate(version)
                    if (release != null) {
                        emit(
                            SettingUiState.Success(
                                appVersion = SettingUiState.AppVersion.NeedUpdate(
                                    release, currentVersion = "v$version"

                                )
                            )
                        )
                    } else {
                        emit(
                            SettingUiState.Success(
                                appVersion = SettingUiState.AppVersion.Newest(
                                    currentVersion = "v$version"
                                )
                            )
                        )
                    }

                } else {
                    emit(SettingUiState.Success(appVersion = SettingUiState.AppVersion.None))
                }
            }.catch {
                it.printStackTrace()
                emit(
                    SettingUiState.Error(
                        error = it,
                        appVersion = _settingUiState.value.appVersion
                    )
                )
            }.collectLatest {
                _settingUiState.emit(it)
            }
        }
    }

    private fun getAppCurrentVersion(): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName, PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }.versionName
        } catch (_: Exception) {
            null
        }
    }

    fun startDownload(url: String) {
        viewModelScope.launch {
            appVersionRepository.downloadApk(" https://ghproxy.com/$url", savePath = context.cacheDir.path).collect {
                val uiState = withContext(Dispatchers.IO) {
                    _settingUiState.value
                }
                when (val version = uiState.appVersion) {
                    is SettingUiState.AppVersion.Downloading -> {
                        _settingUiState.emit(SettingUiState.Success(version.copy(state = it)))
                    }

                    is SettingUiState.AppVersion.NeedUpdate -> {
                        _settingUiState.emit(
                            SettingUiState.Success(
                                SettingUiState.AppVersion.Downloading(
                                    release = version.release,
                                    currentVersion = version.currentVersion,
                                    state = it
                                )
                            )
                        )
                    }
                    else -> {

                    }
                }
            }
        }
    }

}