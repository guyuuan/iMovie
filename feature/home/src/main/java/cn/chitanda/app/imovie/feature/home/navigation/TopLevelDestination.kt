package cn.chitanda.app.imovie.feature.home.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import cn.chitanda.app.imovie.feature.history.navigation.historyNavigationRoute
import cn.chitanda.app.imovie.feature.history.navigation.historyScreen
import cn.chitanda.app.imovie.feature.recently.navigation.recentlyUpdateNavigationRoute
import cn.chitanda.app.imovie.feature.recently.navigation.recentlyUpdateScreen
import cn.chitanda.app.imovie.feature.search.navigation.searchNavigationRoute
import cn.chitanda.app.imovie.feature.search.navigation.searchScreen
import cn.chitanda.app.imovie.feature.setting.navigation.settingNavigationRoute
import cn.chitanda.app.imovie.feature.setting.navigation.settingScreen
import cn.chitanda.app.imovie.core.common.R.string as CommonString

/**
 * @author: Chen
 * @createTime: 2023/2/3 16:21
 * @description:
 **/
enum class TopLevelDestination(
    val icon: ImageVector,
    @StringRes val label: Int,
    @StringRes val title: Int? = null,
    val route: String,
    val navRegistry: (NavGraphBuilder) -> Unit,
) {
    RecentlyUpdate(
        icon = Icons.Default.Home,
        label = CommonString.recently_update_label,
        route = recentlyUpdateNavigationRoute,
        navRegistry = {
            it.recentlyUpdateScreen()
        },
    ),

    Search(
        icon = Icons.Default.Search,
        label = CommonString.search_screen_label,
        route = searchNavigationRoute,
        navRegistry = {
            it.searchScreen()
        },
    ),

    History(
        icon = Icons.Default.History,
        label = CommonString.history_screen_label,
        route = historyNavigationRoute,
        navRegistry = {
            it.historyScreen()
        },
    ) ,
    Setting(
        icon = Icons.Default.Settings,
        label = CommonString.search_screen_label,
        route = settingNavigationRoute,
        navRegistry = {
            it.settingScreen()
        },
    )
}