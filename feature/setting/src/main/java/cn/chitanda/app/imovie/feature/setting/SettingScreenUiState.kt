package cn.chitanda.app.imovie.feature.setting

import cn.chitanda.app.imovie.core.DownloadState
import cn.chitanda.app.imovie.core.model.GithubRelease

/**
 *@author: Chen
 *@createTime: 2023/2/22 22:10
 *@description:
 **/
sealed interface SettingUiState {

    val appVersion: AppVersion

    object Loading : SettingUiState {
        override val appVersion = AppVersion.None
    }

    data class Success(override val appVersion: AppVersion) : SettingUiState

    data class Error(override val appVersion: AppVersion = AppVersion.None, val error: Throwable) :
        SettingUiState

    sealed class AppVersion {
        open val currentVersion: String = "N/A"

        data class NeedUpdate(val release: GithubRelease, override val currentVersion: String) :
            AppVersion()

        data class Downloading(
            val release: GithubRelease,
            val state: DownloadState,
            override val currentVersion: String,
        ) : AppVersion()

        data class Newest(override val currentVersion: String) : AppVersion()
        object None : AppVersion()
    }
}