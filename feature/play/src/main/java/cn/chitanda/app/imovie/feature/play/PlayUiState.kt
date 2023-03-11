package cn.chitanda.app.imovie.feature.play

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.media3.session.MediaController
import cn.chitanda.app.imovie.core.model.HistoryResource
import cn.chitanda.app.imovie.core.model.MovieDetail
import cn.chitanda.app.imovie.ui.state.UiState

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:42
 *@description:
 **/

data class PlayUiState(
    val state: UiState,
    val playInfo: PlayInfo?=null,
    val movie: MovieDetail?=null,
    val history: HistoryResource?=null
)

sealed class PlayInfo(
    open val mediaController: MediaController? = null, open val fullScreen: Boolean = false,
) {
    override fun hashCode(): Int {
        return mediaController.hashCode()+fullScreen.hashCode()
    }
    abstract fun update(
        mediaController: MediaController? = this.mediaController,
        fullScreen: Boolean = this.fullScreen,
    ): PlayInfo

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlayInfo) return false

        if (mediaController != other.mediaController) return false
        if (fullScreen != other.fullScreen) return false

        return true
    }

    data class Ideal(
        override val mediaController: MediaController? = null,
        override val fullScreen: Boolean = false,
    ) : PlayInfo() {
        override fun update(mediaController: MediaController?, fullScreen: Boolean): PlayInfo =
            copy(mediaController = mediaController, fullScreen = fullScreen)
    }


    data class Buffering(
        override val mediaController: MediaController? = null,
        override val fullScreen: Boolean = false,
    ) : PlayInfo() {
        override fun update(mediaController: MediaController?, fullScreen: Boolean): PlayInfo =
            copy(mediaController = mediaController, fullScreen = fullScreen)
    }

    data class Ready(
        override val mediaController: MediaController? = null,
        override val fullScreen: Boolean = false,
    ) : PlayInfo() {
        override fun update(mediaController: MediaController?, fullScreen: Boolean): PlayInfo =
            copy(mediaController = mediaController, fullScreen = fullScreen)
    }

    data class Playing(
        override val mediaController: MediaController? = null,
        override val fullScreen: Boolean = false,
    ) : PlayInfo() {
        override fun update(mediaController: MediaController?, fullScreen: Boolean): PlayInfo =
            copy(mediaController = mediaController, fullScreen = fullScreen)
    }

    data class Pausing(
        override val mediaController: MediaController? = null,
        override val fullScreen: Boolean = false,
    ) : PlayInfo() {
        override fun update(mediaController: MediaController?, fullScreen: Boolean): PlayInfo =
            copy(mediaController = mediaController, fullScreen = fullScreen)
    }

    data class Ending(
        override val mediaController: MediaController? = null,
        override val fullScreen: Boolean = false,
    ) : PlayInfo() {
        override fun update(mediaController: MediaController?, fullScreen: Boolean): PlayInfo =
            copy(mediaController = mediaController, fullScreen = fullScreen)
    }
}

enum class ScreenState {
    Vertical, Horizontal, FullScreen
}

@Composable
fun rememberScreenState(fullScreen: Boolean, landSpace: Boolean) = remember(fullScreen, landSpace) {
    when {
        fullScreen -> ScreenState.FullScreen
        landSpace && !fullScreen -> ScreenState.Horizontal
        else -> ScreenState.Vertical
    }
}
