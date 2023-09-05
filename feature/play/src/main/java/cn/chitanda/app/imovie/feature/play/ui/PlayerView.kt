package cn.chitanda.app.imovie.feature.play.ui

import android.app.PictureInPictureParams
import android.graphics.Rect
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import cn.chitanda.app.imovie.ui.modifier.listenPlayerViewGesture
import cn.chitanda.app.imovie.ui.modifier.rememberPlayerViewGestureState
import cn.chitanda.app.imovie.ui.state.ComposeUiEvent
import cn.chitanda.app.imovie.ui.state.ComposeUiState
import cn.chitanda.app.imovie.ui.util.findActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import timber.log.Timber

/**
 * @author: Chen
 * @createTime: 2023/9/4 14:18
 * @description:
 **/

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun rememberPlayerViewState(
    mediaController: MediaController?, inPip: Boolean = false, fullScreen: Boolean = false
): PlayerViewState {
    var showController by remember {
        mutableStateOf(false)
    }
    var showProgressBar by remember {
        mutableStateOf(false)
    }
    var totalDuration by remember {
        mutableLongStateOf(0L)
    }
    var currentPosition by remember {
        mutableLongStateOf(0L)
    }
    var bufferedPosition by remember {
        mutableLongStateOf(0L)
    }

    var playState by remember {
        mutableStateOf(PlayState.StateIdle)
    }
    var isFullScreen by remember {
        mutableStateOf(fullScreen)
    }
    var isInPip by remember {
        mutableStateOf(inPip)
    }
    var isLongPress by remember {
        mutableStateOf(false)
    }



    mediaController?.contentBufferedPosition

    LaunchedEffect(key1 = playState) {
        if (mediaController != null) {
            while (isActive) {
                delay(500L)
                currentPosition = mediaController.currentPosition
                totalDuration = mediaController.contentDuration
                bufferedPosition = mediaController.bufferedPosition
            }
        }
    }
    LaunchedEffect(key1 = showController) {
        if (showController) {
            delay(3000L)
            showController = false
        }
    }
    DisposableEffect(key1 = mediaController) {
        val listener = object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Timber.d("onIsPlayingChanged: $isPlaying")
                playState = if (isPlaying) {
                    PlayState.StatePlaying
                } else {
                    PlayState.StatePaused
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                Timber.d("onPlaybackStateChanged: $playbackState")
                playState =
                    PlayState.entries[(playbackState - 1).coerceIn(PlayState.entries.indices)]
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                Timber.d("onMediaMetadataChanged: $mediaMetadata")
            }

        }
        mediaController?.addListener(listener)
        onDispose {
            mediaController?.removeListener(listener)
        }
    }
    return PlayerViewState(
        mediaController = mediaController,
        showController = showController,
        showProgressBar = showProgressBar,
        totalDuration = totalDuration,
        currentPosition = currentPosition,
        bufferedPosition = bufferedPosition,
        playState = playState,
        isFullScreen = isFullScreen,
        isInPip = isInPip,
        isLongPress = isLongPress
    ) {
        when (it) {
            PlayerViewEvent.ShowController -> {
                showController = true
            }

            PlayerViewEvent.HideController -> {
                showController = false
            }

            PlayerViewEvent.EnterFullScreen -> {
                isFullScreen = true
            }

            PlayerViewEvent.ExitFullScreen -> {
                isFullScreen = false
            }

            PlayerViewEvent.EnterPip -> {
                isInPip = true
            }

            PlayerViewEvent.ExitPip -> {
                isInPip = false
            }

            is PlayerViewEvent.LongPressChange -> {
                isLongPress = it.press
            }

            else -> {}
        }
    }
}

enum class PlayState {
    StateIdle, StateBuffering, StateReady, StateEnded, StatePaused, StatePlaying;
}

@Stable
data class PlayerViewState(
    val mediaController: MediaController?,
    val showController: Boolean,
    val showProgressBar: Boolean,
    val totalDuration: Long,
    val currentPosition: Long,
    val bufferedPosition: Long,
    val playState: PlayState,
    val isFullScreen: Boolean,
    val isInPip: Boolean,
    val isLongPress: Boolean,
    override val onCollect: (PlayerViewEvent) -> Unit,
) : ComposeUiState<PlayerViewEvent>()

sealed class PlayerViewEvent : ComposeUiEvent() {

    data object ToNext : PlayerViewEvent()
    data object Pause : PlayerViewEvent()
    data object Play : PlayerViewEvent()

    data object ShowController : PlayerViewEvent()
    data object HideController : PlayerViewEvent()
    data object EnterFullScreen : PlayerViewEvent()
    data object ExitFullScreen : PlayerViewEvent()
    data object EnterPip : PlayerViewEvent()
    data object ExitPip : PlayerViewEvent()
    data class LongPressChange(val press: Boolean) : PlayerViewEvent()

}

@Composable
fun ComposePlayerView(
    modifier: Modifier = Modifier,
    state: PlayerViewState,
    windowInsetsPadding: WindowInsets,
) {
    Box(
        modifier = Modifier
            .listenPlayerViewGesture(
                rememberPlayerViewGestureState(onPressChanged = {
                    Timber.d("onPressChanged: $it")
                    state.emit(PlayerViewEvent.LongPressChange(it))
                }, onLeftDelta = {
                    Timber.d("onLeftDelta: $it")
                }, onRightDelta = {
                    Timber.d("onRightDelta: $it")
                }, onHorizontalDelta = {
                    Timber.d("onHorizontalDelta: $it")
                }, onTouch = {
                    state.emit(PlayerViewEvent.ShowController)
                    Timber.d("onTouch")
                }),
            )
            .background(color = Color.Black) then modifier then Modifier.windowInsetsPadding(
            windowInsetsPadding
        ), contentAlignment = Alignment.Center
    ) {
        AndroidPlayerView(state = state)
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clipToBounds(),
            visible = state.showController,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Controller(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.Black.copy(alpha = .4f))
                    .padding(horizontal = 8.dp)
                    .consumeWindowInsets(WindowInsets.navigationBars),
                playState = state.playState,
                currentPosition = state.currentPosition,
                duration = state.totalDuration,
                onPlayButtonClick = {
                    if (it) {
                        state.mediaController?.play()
                    } else {
                        state.mediaController?.pause()
                    }
                },
            )
        }
    }
    LaunchedEffect(key1 = Unit) {
        state.emit(PlayerViewEvent.ShowController)
    }
}

@Composable
private fun Controller(
    modifier: Modifier = Modifier,
    playState: PlayState,
    currentPosition: Long,
    duration: Long,
    onPlayButtonClick: (Boolean) -> Unit
) {
    val progress by remember {
        derivedStateOf {
            if (duration == 0L) {
                0f
            } else {
                currentPosition / duration.toFloat()
            }
        }
    }
    val isPlaying by remember(playState) {
        derivedStateOf {
            playState == PlayState.StatePlaying
        }
    }
    Column(modifier = modifier) {
        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
        Row {
            IconButton(onClick = {
                onPlayButtonClick(!isPlaying)
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.size(36.dp),
                )
            }
        }
    }
}

@Composable
fun AndroidPlayerView(
    modifier: Modifier = Modifier,
    state: PlayerViewState,
) {
    val activity = findActivity()
    AndroidView(modifier = modifier, factory = {
        PlayerView(it).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            useController = false
            addOnLayoutChangeListener {
                    _, left, top, right, bottom,
                    oldLeft, oldTop, oldRight, oldBottom,
                ->
                if (left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom) {
                    // The playerView's bounds changed, update the source hint rect to
                    // reflect its new bounds.
                    val sourceRectHint = Rect()
                    getGlobalVisibleRect(sourceRectHint)
                    activity?.setPictureInPictureParams(
                        PictureInPictureParams.Builder().setSourceRectHint(sourceRectHint).build()
                    )
                }
            }
        }
    }) {
        it.player = state.mediaController

    }
}