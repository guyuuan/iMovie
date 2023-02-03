package cn.chitanda.app.imovie.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cn.chitanda.app.imovie.feature.home.HomeScreen

/**
 * @author: Chen
 * @createTime: 2023/2/3 14:03
 * @description:
 **/
const val homeNavigationRoute = "home_route"

fun NavGraphBuilder.homeScreen() {
    composable(homeNavigationRoute) {
        HomeScreen()
    }
}