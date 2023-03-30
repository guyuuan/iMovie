package cn.chitanda.app.imovie.feature.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cn.chitanda.app.imovie.feature.recently.navigation.recentlyUpdateNavigationRoute

/**
 * @author: Chen
 * @createTime: 2023/2/3 14:01
 * @description:
 **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val homeNavController = rememberNavController()
    val homeScreenState = rememberHomeScreenState(navController = homeNavController)
    Scaffold(
        topBar = {
            if (homeScreenState.showTopAppBar) {
                CenterAlignedTopAppBar(title = { Text(text = "iMovie") })
            }
        },
        bottomBar = {
            HomeBottomBar(homeScreenState)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        NavHost(
            modifier = Modifier.padding(it),
            navController = homeNavController,
            startDestination = recentlyUpdateNavigationRoute
//            startDestination = settingNavigationRoute
        ) {
            homeScreenState.topLevelDestinations.forEach { destination ->
                destination.navRegistry(this)
            }
        }
    }
}


@Composable
private fun HomeBottomBar(homeScreenState: HomeScreenState) {
    NavigationBar(windowInsets = WindowInsets.navigationBars) {
        homeScreenState.topLevelDestinations.forEach { destination ->
            NavigationBarItem(
                selected = homeScreenState.currentTopLevelDestination == destination,
                onClick = {
                    homeScreenState.navigationToTopLevelDestination(destination)
                },
                icon = {
                    Icon(
                        destination.icon,
                        contentDescription = stringResource(id = destination.label)
                    )
                },
                label = { Text(text = stringResource(id = destination.label)) })
        }
    }
}