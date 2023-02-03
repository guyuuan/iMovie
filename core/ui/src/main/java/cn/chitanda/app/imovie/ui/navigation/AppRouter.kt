package cn.chitanda.app.imovie.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

/**
 * @author: Chen
 * @createTime: 2023/2/3 10:01
 * @description:
 **/
@Composable
fun AppRouter(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit
) {
    CompositionLocalProvider(
        LocalNavController provides navController,
    ) {
        NavHost(
            navController = navController, startDestination = startDestination, builder = builder
        )
    }
}

@Composable
fun NavigationRegistry(
    navigationToPlay: NavigationToPlay, content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalNavigateToPlayScreen provides navigationToPlay, content = content)
}