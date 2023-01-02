package cn.chitanda.app.imovie.feature.play.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.chitanda.app.imovie.feature.play.PlayScreen

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:05
 *@description:
 **/

internal const val playIdArgs = "playId"
internal const val playScreenRoute = "play_screen_route"

internal class PlayArgs(val playId: Long) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        playId = checkNotNull(savedStateHandle[playIdArgs])
    )
}

fun NavController.navigateToPlay(playId: Long) {
    this.navigate("$playScreenRoute/$playId")
}

fun NavGraphBuilder.playScreen() {
    1.0f .. 1.4f
    1 until  2
    composable(
        route = "$playScreenRoute/{$playIdArgs}",
        arguments = listOf(
            navArgument(playIdArgs) { type = NavType.LongType }
        )
    ) {
        PlayScreen()
    }
}