package cn.chitanda.app.imovie.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cn.chitanda.app.imovie.feature.search.SearchScreen

/**
 * @author: Chen
 * @createTime: 2023/2/3 14:56
 * @description:
 **/
const val searchNavigationRoute = "search_route"


fun NavGraphBuilder.searchScreen() {
    composable(searchNavigationRoute) {
        SearchScreen()
    }
}