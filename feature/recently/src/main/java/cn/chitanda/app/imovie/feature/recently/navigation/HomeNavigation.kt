package cn.chitanda.app.imovie.feature.recently.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.chitanda.app.imovie.feature.recently.RecentlyUpdateScreen

/**
 *@author: Chen
 *@createTime: 2022/11/20 12:35
 *@description:
 **/

const val recentlyUpdateNavigationRoute = "recently_update_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(recentlyUpdateNavigationRoute, navOptions)
}

fun NavGraphBuilder.recentlyUpdateScreen() {
    composable(route = recentlyUpdateNavigationRoute) {
        RecentlyUpdateScreen()
    }
}