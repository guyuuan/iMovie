package cn.chitanda.app.imovie.feature.play.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
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
internal const val playScreenRoute = "play/{$playIdArgs}?playFromHistory={$playFromHistoryArgs}"

internal class PlayArgs(val playId: Long, val playFromHistory: Boolean) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        playId = checkNotNull(savedStateHandle[playIdArgs]),
        playFromHistory = checkNotNull(savedStateHandle[playFromHistoryArgs])
    )
}

fun NavController.navigateToPlay(
    playId: Long,
    playFromHistory: Boolean,
    navOptions: NavOptions? = null
) {
    this.navigate("play/$playId?playFromHistory=$playFromHistory", navOptions)
}

fun NavGraphBuilder.playScreen() {
    composable(
        route = playScreenRoute,
//        route = "$playScreenRoute/{$playIdArgs}?playFromHistory={$playFromHistoryArgs}",
        arguments = listOf(
            navArgument(playIdArgs) { type = NavType.LongType },
            navArgument(playFromHistoryArgs) { type = NavType.BoolType }
        )
    ) {
        PlayScreen()
    }
}