package cn.chitanda.app.imovie.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.chitanda.app.imovie.feature.home.HomeScreen

/**
 *@author: Chen
 *@createTime: 2022/11/20 12:35
 *@description:
 **/
typealias NavigationToPlay =(Long) -> Unit
const val homeNavigationRoute = "home_route"
fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(navigationToPlay: NavigationToPlay) {
    composable(route = homeNavigationRoute) {
        HomeScreen(navigationToPlay = navigationToPlay)
    }
}