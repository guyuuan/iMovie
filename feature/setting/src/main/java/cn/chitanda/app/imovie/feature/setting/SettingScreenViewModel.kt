package cn.chitanda.app.imovie.feature.setting

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.chitanda.app.imovie.core.data.repository.AppVersionRepository
import cn.chitanda.app.imovie.core.datastore.AppPreferencesDataSource
import cn.chitanda.app.imovie.core.ext.safeLaunch
import cn.chitanda.app.imovie.ui.ext.setState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
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
    private val appPreferences: AppPreferencesDataSource,
    private val context: Application,
) : ViewModel() {
    private val _settingUiState: MutableStateFlow<SettingUiState> =
        MutableStateFlow(
            SettingUiState(
                settings = AppSetting.getDefaultList(context = context, aboutPagePath = "") {
                    safeLaunch {
                        appPreferences.setUsePip(it)
                    }
                }
            )

        )
    val settingUiState: StateFlow<SettingUiState> get() = _settingUiState

    private val versionCheckFlow = flow {
        val version = getAppCurrentVersion()
        if (version != null) {
            emit(
                SettingUiState.AppVersion.Newest(
                    currentVersion = "v$version"
                )
            )
            val release = appVersionRepository.checkAppNeedUpdate(version)
            if (release != null) {
                emit(
                    SettingUiState.AppVersion.NeedUpdate(
                        release, currentVersion = "v$version"

                    )
                )
            } else {
                emit(
                    SettingUiState.AppVersion.Newest(
                        currentVersion = "v$version"
                    )
                )
            }

        } else {
            emit(SettingUiState.AppVersion.None)
        }
    }.catch {
        it.printStackTrace()
        emit(
            _settingUiState.value.appVersion
        )
    }

    init {
        viewModelScope.launch {
            appPreferences.enablePip.stateIn(viewModelScope)
                .combine(versionCheckFlow) { enablePip, version ->
                    _settingUiState.setState {
                        copy(
                            appVersion = version,
                            settings = settings.updateByName<AppSetting.Switcher>("pip") {
                                copy(state = enablePip)
                            })
                    }
                }.collect()
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
            appVersionRepository.downloadApk(
                " https://ghproxy.com/$url", savePath = context.cacheDir.path
            ).collect {
                val uiState = withContext(Dispatchers.IO) {
                    _settingUiState.value
                }
                when (val version = uiState.appVersion) {
                    is SettingUiState.AppVersion.Downloading -> {
                        _settingUiState.setState {
                            copy(appVersion = version.copy(state = it))
                        }
                    }

                    is SettingUiState.AppVersion.NeedUpdate -> {
                        _settingUiState.setState {
                            copy(
                                appVersion = SettingUiState.AppVersion.Downloading(
                                    release = version.release,
                                    currentVersion = version.currentVersion,
                                    state = it
                                )
                            )
                        }
                    }

                    else -> {

                    }
                }
            }
        }
    }

}

inline fun <reified T : AppSetting> List<AppSetting>.updateByName(
    name: String, update: T.() -> T
): List<AppSetting> {
    val setting = (this.find { it.name == name }.takeIf { it is T } as? T)
        ?: error("can't find setting by name = $name")
    val index = indexOf(setting)
    val new = toMutableList()
    new[index] = setting.update()
    return new
}