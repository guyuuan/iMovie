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
internal const val playFromHistoryArgs = "play_from_history"
internal const val playScreenRoute = "play_screen_route"

internal class PlayArgs(val playId: Long, val playFromHistory: Boolean) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        playId = checkNotNull(savedStateHandle[playIdArgs]),
        playFromHistory = checkNotNull(savedStateHandle[playFromHistoryArgs])
    )
}

fun NavController.navigateToPlay(playId: Long, playFromHistory: Boolean) {
    this.navigate("$playScreenRoute/$playId/$playFromHistory")
}

fun NavGraphBuilder.playScreen() {
    composable(
        route = "$playScreenRoute/{$playIdArgs}/{$playFromHistoryArgs}",
        arguments = listOf(
            navArgument(playIdArgs) { type = NavType.LongType },
            navArgument(playFromHistoryArgs) { type = NavType.BoolType }
        )
    ) {
        PlayScreen()
    }
}