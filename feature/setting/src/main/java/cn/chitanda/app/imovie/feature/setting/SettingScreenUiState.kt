package cn.chitanda.app.imovie.feature.setting

import android.content.Context
import cn.chitanda.app.imovie.core.DownloadState
import cn.chitanda.app.imovie.core.model.GithubRelease
import cn.chitanda.app.imovie.ui.state.UiState
import cn.chitanda.app.imovie.core.common.R.string as RString

/**
 *@author: Chen
 *@createTime: 2023/2/22 22:10
 *@description:
 **/
data class SettingUiState(
    val appVersion: AppVersion = AppVersion.None,
    val settings: List<AppSetting> = emptyList(),
    val state: UiState = UiState.Loading
) {

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

sealed interface AppSetting {
    val name: String
    val title: String
    val subTitle: String?
        get() = null

    data class Switcher(
        override val title: String,
        override val name: String,
        override val subTitle: String? = null,
        val state: Boolean,
        val onChange: (Boolean) -> Unit
    ) : AppSetting

    data class NavigationItem(
        override val title: String,
        override val name: String,
        val path: String,
        override val subTitle: String? = null
    ) : AppSetting

    companion object {
        fun getDefaultList(context: Context, aboutPagePath:String, onPipModeChange:(Boolean)->Unit): List<AppSetting> {
            return listOf(
                Switcher(
                    name = "pip",
                    title = context.getString(RString.app_setting_pip),
                    onChange = onPipModeChange,
                    state = false
                ),
                NavigationItem(
                    name = "about",
                    title = context.getString(RString.app_setting_about),
                    path = aboutPagePath
                ),
            )
        }
    }
}