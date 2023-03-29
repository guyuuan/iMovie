package cn.chitanda.app.imovie.feature.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cn.chitanda.app.imovie.feature.setting.AboutScreen
import cn.chitanda.app.imovie.feature.setting.LicenseScreen
import cn.chitanda.app.imovie.feature.setting.SettingScreen

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:46
 * @description:
 **/

const val settingNavigationRoute = "setting_route"
const val aboutNavigationRoute = "about_route"
const val licenseNavigationRoute = "license_route"
fun NavGraphBuilder.settingScreen() {
    composable(settingNavigationRoute) {
        SettingScreen()
    }
}

fun NavGraphBuilder.aboutScreen() {
    composable(aboutNavigationRoute) {
        AboutScreen()
    }
}
fun NavGraphBuilder.licenseScreen() {
    composable(licenseNavigationRoute) {
        LicenseScreen()
    }
}