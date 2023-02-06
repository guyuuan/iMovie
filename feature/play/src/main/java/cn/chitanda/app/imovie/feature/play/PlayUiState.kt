package cn.chitanda.app.imovie.feature.play

import androidx.media3.session.MediaController
import cn.chitanda.app.imovie.core.model.HistoryResource
import cn.chitanda.app.imovie.core.model.MovieDetail

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:42
 *@description:
 **/
sealed interface PlayUiState {
    val playInfo: PlayInfo?
    fun update(playInfo: PlayInfo?, history: HistoryResource?=null): PlayUiState
    data class Success(
        val movie: MovieDetail,
        val history: HistoryResource? = null,
        override val playInfo: PlayInfo = PlayInfo.Ideal(),
    ) : PlayUiState {
        override fun update(playInfo: PlayInfo?, history: HistoryResource?) =
            if (playInfo == null) {
                Failed(error = Error("update play info is null"))
            } else {
                copy(
                    history = this.history?:history,
                    playInfo = playInfo,
                )
            }
    }

    data class Failed(val error: Throwable? = null, override val playInfo: PlayInfo? = null) :
        PlayUiState {
        override fun update(playInfo: PlayInfo?, history: HistoryResource?) =
            copy(playInfo = playInfo)
    }

    data class Loading(override val playInfo: PlayInfo? = null) : PlayUiState {
        override fun update(playInfo: PlayInfo?, history: HistoryResource?) =
            if (playInfo == null) Failed(error = Error("update play info is null")) else copy(
                playInfo = playInfo
            )
    }
}

sealed class PlayInfo(
    open val mediaController: MediaController? = null, open val fullScreen: Boolean = false,
) {
    abstract fun update(
        mediaController: MediaController? = this.mediaController,
        fullScreen: Boolean = this.fullScreen,
    ): PlayInfo

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