package cn.chitanda.app.imovie.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import cn.chitanda.app.imovie.ui.viewmodel.MainViewModel

/**
 * @author: Chen
 * @createTime: 2023/2/3 10:08
 * @description:
 **/
typealias NavigationToPlay = (Long, Boolean) -> Unit

val LocalNavController = compositionLocalOf<NavController> {
    error("Could not find LocalNavController")
}

val LocalNavigateToPlayScreen = compositionLocalOf<NavigationToPlay> {
    error("Could not find NavigationToPlay")
}

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> {
    error("Could not find MainViewModel")
}