package cn.chitanda.app.imovie.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

/**
 * @author: Chen
 * @createTime: 2023/2/3 10:08
 * @description:
 **/
typealias NavigationToPlay = (Long) -> Unit

val LocalNavController = compositionLocalOf <NavController>{
    error("Could not find LocalNavController")
}

val LocalNavigateToPlayScreen = compositionLocalOf<NavigationToPlay> {
    error("Could not find NavigationToPlay")
}