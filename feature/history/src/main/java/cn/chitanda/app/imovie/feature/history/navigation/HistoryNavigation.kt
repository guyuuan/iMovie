package cn.chitanda.app.imovie.feature.history.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cn.chitanda.app.imovie.feature.history.HistoryScreen
import cn.chitanda.app.imovie.feature.history.R

/**
 * @author: Chen
 * @createTime: 2023/2/3 14:52
 * @description:
 **/
const val historyNavigationRoute = "history_route"
fun NavGraphBuilder.historyScreen() {
    composable(historyNavigationRoute) {
        HistoryScreen()
    }
}