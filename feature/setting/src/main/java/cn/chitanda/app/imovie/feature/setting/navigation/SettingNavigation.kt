package cn.chitanda.app.imovie.feature.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cn.chitanda.app.imovie.feature.setting.SettingScreen

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:46
 * @description:
 **/

const val settingNavigationRoute = "setting_route"
fun NavGraphBuilder.settingScreen() {
    composable(settingNavigationRoute) {
        SettingScreen()
    }
}