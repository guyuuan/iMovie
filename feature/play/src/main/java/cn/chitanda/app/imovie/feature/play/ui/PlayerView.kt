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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.toDateTimePeriod
import timber.log.Timber
import kotlin.math.roundToLong
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
    val coroutineScope = rememberCoroutineScope()
    var showController by remember {
        mutableStateOf(false)
    }
    var title: String? by remember {
        mutableStateOf(null)
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

    var volume: Float? by remember {
        mutableStateOf(null)
    }
    var brightness: Float? by remember {
        mutableStateOf(null)
    }
    var seekToPosition: Long? by remember {
        mutableStateOf(null)
    }

    val dragFlow = remember {
        Channel<DragDelta>(Channel.UNLIMITED)
    }
    val activity = findActivity()
    LaunchedEffect(key1 = playState) {
        if (mediaController != null) {
            while (isActive) {
                delay(100L)
                currentPosition = mediaController.currentPosition
                totalDuration = mediaController.contentDuration
                bufferedPosition = mediaController.bufferedPosition
            }
        }
    }
    LaunchedEffect(showController, seekToPosition) {
        if (showController) {
            delay(5000L)
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
                title = mediaMetadata.title?.toString()
            }

        }
        mediaController?.addListener(listener)
        onDispose {
            mediaController?.removeListener(listener)
        }
    }
    LaunchedEffect(seekToPosition) {
        if (seekToPosition != null) {
            delay(1000L)
            seekToPosition = null
        }
    }
    LaunchedEffect(dragFlow, activity, totalDuration, mediaController) {
        launch(Dispatchers.Main) {
            var temp = 0f
            while (isActive) {
                var delta = dragFlow.receive()
                when (delta) {
                    is DragDelta.Brightness -> {
                        val currentBrightness =
                            activity?.window?.attributes?.screenBrightness ?: continue
                        brightness = (currentBrightness + delta.delta).also { f ->
                            val attr = activity.window.attributes
                            attr.screenBrightness = f
                            activity.window.attributes = attr
                        }

                    }

                    is DragDelta.Seek -> {
                        if (totalDuration == 0L) continue
                        showController = true
                        while (delta !is DragDelta.Cancel && delta is DragDelta.Seek && mediaController?.currentPosition != null) {
                            temp += (delta.delta)
                            val cache = seekToPosition ?: mediaController.currentPosition
                            seekToPosition =
                                (cache + (totalDuration * temp).roundToLong()).coerceIn(
                                    0, totalDuration
                                )
                            Timber.d("collect: $delta, $seekToPosition")
                            delta = dragFlow.receive()
                        }
                        Timber.d("cancel: $delta,$seekToPosition")
                        temp = 0f
                        if (seekToPosition != null) {
                            mediaController?.seekTo(
                                seekToPosition ?: continue
                            )
                        }
                    }

                    is DragDelta.Volume -> {
                        volume = mediaController?.volume?.plus(delta.delta)?.also { v ->
                            mediaController.volume = v
                        }
                    }

                    DragDelta.Cancel -> {

                        volume = null
                        brightness = null
                    }
                }
            }

        }
    }
    return PlayerViewState(
        mediaController = mediaController,
        title = title,
        showController = showController,
        totalDuration = totalDuration,
        currentPosition = currentPosition,
        bufferedPosition = bufferedPosition,
        playState = playState,
        isFullScreen = isFullScreen,
        isInPip = isInPip,
        isLongPress = isLongPress,
        volume = volume,
        brightness = brightness,
        seekToPosition = seekToPosition
    ) {
        when (it) {
            PlayerViewEvent.ShowController -> {
                showController = !showController
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

            is PlayerViewEvent.Seek -> {
                if (totalDuration > 0L) {
                    coroutineScope.launch {
                        dragFlow.trySend(DragDelta.Seek(it.percent))
                    }
                }
            }

            is PlayerViewEvent.Brightness -> {
                coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                    dragFlow.trySend(DragDelta.Brightness(it.percent))
                }
            }

            is PlayerViewEvent.Volume -> {
                coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                    dragFlow.trySend(DragDelta.Volume(it.percent))
                }
            }

            PlayerViewEvent.DragStopped -> {
                coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                    dragFlow.trySend(DragDelta.Cancel)
                }
            }
        }
    }
}

sealed class DragDelta {
    data class Brightness(val delta: Float) : DragDelta()
    data class Volume(val delta: Float) : DragDelta()
    data class Seek(val delta: Float) : DragDelta()

    data object Cancel : DragDelta()
}

enum class PlayState {
    StateIdle, StateBuffering, StateReady, StateEnded, StatePaused, StatePlaying;
}

@Stable
data class PlayerViewState(
    val mediaController: MediaController?,
    val title: String?,
    val showController: Boolean,
    val totalDuration: Long,
    val currentPosition: Long,
    val bufferedPosition: Long,
    val playState: PlayState,
    val isFullScreen: Boolean,
    val isInPip: Boolean,
    val isLongPress: Boolean,
    val volume: Float?,
    val brightness: Float?,
    val seekToPosition: Long?,
    override val onCollect: (PlayerViewEvent) -> Unit,
) : ComposeUiState<PlayerViewEvent>()

sealed class PlayerViewEvent : ComposeUiEvent() {

    data object ShowController : PlayerViewEvent()
    data object HideController : PlayerViewEvent()
    data object EnterFullScreen : PlayerViewEvent()
    data object ExitFullScreen : PlayerViewEvent()
    data object EnterPip : PlayerViewEvent()
    data object ExitPip : PlayerViewEvent()
    data class LongPressChange(val press: Boolean) : PlayerViewEvent()

    data class Seek(val percent: Float) : PlayerViewEvent()
    data class Brightness(val percent: Float) : PlayerViewEvent()
    data class Volume(val percent: Float) : PlayerViewEvent()

    data object DragStopped : PlayerViewEvent()
}

@Composable
fun ComposePlayerView(
    modifier: Modifier = Modifier,
    state: PlayerViewState,
    windowInsetsPadding: WindowInsets,
    onPressBack: () -> Unit,
    onFullScreenChanged: (Boolean) -> Unit = {},
) {
    val showSeekTo = remember(state.seekToPosition) {
        state.seekToPosition != null
    }
    Box(
        modifier = Modifier
            .listenPlayerViewGesture(rememberPlayerViewGestureState(onPressChanged = {
                Timber.d("onPressChanged: $it")
                state.emit(PlayerViewEvent.LongPressChange(it))
            }, onLeftDelta = {
                state.emit(PlayerViewEvent.Brightness(it))
                Timber.d("onLeftDelta: $it")
            }, onRightDelta = {
                state.emit(PlayerViewEvent.Volume(it))
                Timber.d("onRightDelta: $it")
            }, onHorizontalDelta = {
                state.emit(PlayerViewEvent.Seek(it / 3))
                Timber.d("onHorizontalDelta: $it")
            }, onTouch = {
                state.emit(PlayerViewEvent.ShowController)
                Timber.d("onTouch")
            }), onDragStopped = {
                state.emit(PlayerViewEvent.DragStopped)
                Timber.d("onDragStopped: ")
            })
            .background(color = Color.Black) then modifier then Modifier.windowInsetsPadding(
            windowInsetsPadding
        ), contentAlignment = Alignment.Center
    ) {
        AndroidPlayerView(state = state)
        AnimatedVisibility(modifier = Modifier
            .align(Alignment.TopCenter)
            .clipToBounds(),
            visible = state.showController,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut()) {
            TopController(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = .5f), Color.Black.copy(alpha = 0f)
                            )
                        )
                    )
                    .padding(horizontal = 8.dp),
                title = state.title,
                onPressBack = onPressBack,
            )
        }
        AnimatedVisibility(modifier = Modifier
            .align(Alignment.BottomCenter)
            .clipToBounds(),
            visible = state.showController || showSeekTo,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()) {
            Controller(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0f), Color.Black.copy(.5f))
                        )
                    )
                    .padding(horizontal = 4.dp),
                playState = state.playState,
                isFullScreen = state.isFullScreen,
                currentPosition = state.seekToPosition ?: state.currentPosition,
                duration = state.totalDuration,
                onPlayButtonClick = {
                    if (it) {
                        state.mediaController?.play()
                    } else {
                        state.mediaController?.pause()
                    }
                },
                onFullScreenButtonClick = {
                    if (it) {
                        state.emit(PlayerViewEvent.EnterFullScreen)
                    } else {
                        state.emit(PlayerViewEvent.ExitFullScreen)
                    }
                    onFullScreenChanged(it)
                },
            )
        }
        AnimatedVisibility(
            visible = showSeekTo,
            modifier = Modifier
                .align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SeekToFloatToast(
                modifier = Modifier.background(
                    Color.Black.copy(alpha = .5f), shape = MaterialTheme.shapes.medium
                ), seekToPosition = state.seekToPosition, totalDuration = state.totalDuration
            )
        }

    }
    LaunchedEffect(key1 = Unit) {
        state.emit(PlayerViewEvent.ShowController)
    }
}

@Composable
fun SeekToFloatToast(modifier: Modifier = Modifier, seekToPosition: Long?, totalDuration: Long) {
    var seek by remember {
        mutableStateOf(seekToPosition?.toTimeString())
    }
    val total = remember(totalDuration) {
        "/${totalDuration.toTimeString()}"
    }
    Box(
        modifier = modifier
    ) {
        Text(
            text = "$seek$total",
            style = TextStyle(color = Color.White),
            modifier = Modifier.padding(16.dp)
        )
    }
    LaunchedEffect(seekToPosition) {
        if (seekToPosition != null) {
            seek = seekToPosition.toTimeString()
        }
    }
}

@Composable
private fun TopController(
    modifier: Modifier = Modifier, title: String?, onPressBack: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPressBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White,
            )
        }
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun Controller(
    modifier: Modifier = Modifier,
    playState: PlayState, isFullScreen: Boolean,
    currentPosition: Long,
    duration: Long, onPlayButtonClick: (Boolean) -> Unit, onFullScreenButtonClick: (Boolean) -> Unit
) {
    val isPlaying by remember(playState) {
        derivedStateOf {
            playState == PlayState.StatePlaying
        }
    }
    ProvideTextStyle(
        value = TextStyle(
            color = Color.White, fontSize = 12.sp
        )
    ) {
        if (isFullScreen) {
            FullScreenBottomController(
                modifier,
                isPlaying,
                currentPosition,
                duration,
                onPlayButtonClick,
                onFullScreenButtonClick
            )
        } else {
            SimpleBottomController(
                modifier,
                isPlaying,
                currentPosition,
                duration,
                onPlayButtonClick,
                onFullScreenButtonClick
            )
        }
    }
}

@Composable
private fun FullScreenBottomController(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayButtonClick: (Boolean) -> Unit,
    onFullScreenButtonClick: (Boolean) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            LinearProgressIndicator(progress = progress, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PlayPauseButton(isPlaying = isPlaying, onPlayButtonClick = onPlayButtonClick)
            FullScreenButton(isFullScreen = true, onFullScreenButtonClick = onFullScreenButtonClick)
        }
    }
}

@Composable
private fun SimpleBottomController(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onPlayButtonClick: (Boolean) -> Unit,
    onFullScreenButtonClick: (Boolean) -> Unit
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayPauseButton(isPlaying = isPlaying, onPlayButtonClick = onPlayButtonClick)
        val progress by remember {
            derivedStateOf {
                if (duration == 0L) {
                    0f
                } else {
                    currentPosition / duration.toFloat()
                }
            }
        }
        LinearProgressIndicator(
            progress = progress,
            Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )
        val total = remember { "/${duration.toTimeString()}" }
        Text("${currentPosition.toTimeString()}$total")
        FullScreenButton(
            isFullScreen = false, onFullScreenButtonClick = onFullScreenButtonClick
        )
    }
}

/*
 * @return: String "mm:ss" if time>1h "hh:mm:ss"
 * */
private fun Long.toTimeString(): String {
    val time = this.toDuration(DurationUnit.MILLISECONDS).toDateTimePeriod()
    return "${
        time.hours.takeIf { it > 0 }?.let { "${it.toString().padStart(2, '0')}:" } ?: ""
    }${time.minutes.toString().padStart(2, '0')}:${time.seconds.toString().padStart(2, '0')}"
}

@Composable
private fun PlayPauseButton(
    modifier: Modifier = Modifier, isPlaying: Boolean, onPlayButtonClick: (Boolean) -> Unit
) {
    IconButton(modifier = modifier, onClick = {
        onPlayButtonClick(!isPlaying)
    }) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
        )
    }
}

@Composable
private fun FullScreenButton(
    modifier: Modifier = Modifier, isFullScreen: Boolean, onFullScreenButtonClick: (Boolean) -> Unit
) {
    IconButton(modifier = modifier, onClick = {
        onFullScreenButtonClick(!isFullScreen)
    }) {
        Icon(
            imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
            contentDescription = null,
            tint = Color.White,
        )
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