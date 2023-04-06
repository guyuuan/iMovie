@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package cn.chitanda.app.imovie.feature.play

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import cn.chitanda.app.imovie.core.design.windowsize.LocalWindowSizeClass
import cn.chitanda.app.imovie.core.model.MovieDetail
import cn.chitanda.app.imovie.core.model.PlaysSet
import cn.chitanda.app.imovie.ui.ext.collectPartAsState
import cn.chitanda.app.imovie.ui.ext.zero
import cn.chitanda.app.imovie.ui.navigation.LocalMainViewModel
import cn.chitanda.app.imovie.ui.navigation.LocalNavController
import cn.chitanda.app.imovie.ui.state.UiState
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

/**
 *@author: Chen
 *@createTime: 2022/11/22 16:51
 *@description:
 **/

@Composable
fun PlayScreen(viewModel: PlayScreenViewModel = hiltViewModel()) {
    val uiState by viewModel.playUiState.collectPartAsState(part = PlayUiState::state)
    val playInfo by viewModel.playUiState.collectPartAsState(part = PlayUiState::playInfo)
    val movie by viewModel.playUiState.collectPartAsState(part = PlayUiState::movie)
    val sizeClass = LocalWindowSizeClass.current.widthSizeClass
    val fullScreen by remember(playInfo) {
        derivedStateOf {
            playInfo?.fullScreen ?: false
        }
    }
    val systemBarController = rememberSystemUiController()
    val coroutineScope = rememberCoroutineScope()
    val isInPip by LocalMainViewModel.current.isInPictureInPictureMode.collectAsState()
    val activity = LocalContext.current as Activity

    val navController = LocalNavController.current
    val owner = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    val isLandScape = sizeClass == WindowWidthSizeClass.Expanded
    val screenState =
        rememberScreenState(fullScreen = fullScreen, landSpace = isLandScape, isInPip = isInPip)
    val transition = updateTransition(targetState = screenState, label = "play_screen_animation")
    val horizontalScreenBodyWeight by transition.animateFloat(label = "horizontal_screen_body_weight") { state ->
        when (state) {
            ScreenState.Horizontal -> 2f
            else -> 0.0001f
        }
    }
    val verticalScreenBodyWeight by transition.animateFloat(label = "vertical_screen_body_weight") { state ->
        when (state) {
            ScreenState.Vertical -> 3f
            else -> 0.0001f
        }
    }
    val mediaController: MediaController? by remember(playInfo) {
        derivedStateOf {
            playInfo?.mediaController
        }
    }

    Scaffold(contentWindowInsets = WindowInsets.zero()) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Row(modifier = Modifier.weight(1f)) {
                VideoView(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(3f),
                    windowInsetsPadding = when (screenState) {
                        ScreenState.Vertical -> WindowInsets.statusBars
                        ScreenState.Horizontal -> WindowInsets.systemBars
                        else -> WindowInsets.zero()
                    },
                    isInPip = isInPip,
                    viewModel = viewModel,
                    mediaController = mediaController,
                    fullScreen = fullScreen,
                )
                transition.AnimatedVisibility(visible = { state ->
                    state == ScreenState.Horizontal
                },
                    modifier = Modifier.weight(horizontalScreenBodyWeight),
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally { it }) {
                    ScreenBody(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars),
                        uiState = uiState,
                        playInfo = playInfo,
                        movie = movie,
                        viewModel = viewModel
                    )
                }
            }
            transition.AnimatedVisibility(visible = { state ->
                state == ScreenState.Vertical
            },
                modifier = Modifier.weight(verticalScreenBodyWeight),
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }) {
                ScreenBody(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    uiState = uiState,
                    playInfo = playInfo,
                    movie = movie,
                    viewModel = viewModel
                )
            }

        }
    }

    LaunchedEffect(key1 = playInfo) {
        if (playInfo is PlayInfo.Playing || playInfo is PlayInfo.Buffering) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(key1 = fullScreen) {
        if (fullScreen) {
            hideSystemBar(systemBarController)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            showSystemBar(systemBarController)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                if (playInfo is PlayInfo.Pausing) {
                    playInfo?.mediaController?.play()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                if (playInfo is PlayInfo.Playing) {
                    playInfo?.mediaController?.pause()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    DisposableEffect(key1 = owner) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.handleOnBackPressed()) {
                    viewModel.changeFullScreenState()
                    showSystemBar(systemBarController)
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                } else {
                    coroutineScope.launch {
                        viewModel.updateHistory()
                        navController.navigateUp()
                    }
                }
            }
        }
        owner?.addCallback(callback)
        onDispose {
            callback.isEnabled = false
        }
    }
}

@Composable
private fun ScreenBody(
    modifier: Modifier = Modifier,
    uiState: UiState,
    playInfo: PlayInfo?,
    movie: MovieDetail?,
    viewModel: PlayScreenViewModel,
) {
    Surface(modifier = modifier) {
        when (uiState) {
            is UiState.Success -> {
                MovieDetailBody(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    playInfo = playInfo,
                    onPlaysSetClick = {
                        viewModel.play(it)
                    },
                    movie = movie!!
                )
            }

            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        text = uiState.error.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private const val TAG = "PlayScreen"

@Composable
fun VideoView(
    windowInsetsPadding: WindowInsets,
    viewModel: PlayScreenViewModel,
    modifier: Modifier = Modifier,
    isInPip: Boolean,
    fullScreen: Boolean,
    mediaController: MediaController?
) {
    var showAppBar by rememberSaveable { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .background(color = Color.Black)
                then modifier
                then Modifier.windowInsetsPadding(windowInsetsPadding)
    ) {
        AndroidVideoView(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            isInPip = isInPip,
            mediaController = mediaController
        ) {
            showAppBar = it
        }
        AnimatedVisibility(
            visible = showAppBar && !fullScreen && !isInPip,
            enter = slideInHorizontally { -it } + fadeIn(),
            exit = slideOutHorizontally { -it } + fadeOut()
        ) {
            val navController = LocalNavController.current
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.Default.ArrowBack,
                    tint = Color.White,
                    contentDescription = "navigate up"
                )
            }
        }
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun AndroidVideoView(
    modifier: Modifier = Modifier,
    viewModel: PlayScreenViewModel,
    isInPip: Boolean,
    mediaController: MediaController?,
    onControllerVisibilityChange: (Boolean) -> Unit
) {
    val activity = LocalContext.current as ComponentActivity
    AndroidView(modifier = modifier, factory = {
        PlayerView(it).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                onControllerVisibilityChange(
                    visibility == View.VISIBLE
                )
            })
            showController()
            setFullscreenButtonClickListener { _ ->
                viewModel.changeFullScreenState()
            }
            addOnLayoutChangeListener {
                    _, left, top, right, bottom,
                    oldLeft, oldTop, oldRight, oldBottom,
                ->
                if (left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom) {
                    // The playerView's bounds changed, update the source hint rect to
                    // reflect its new bounds.
                    val sourceRectHint = Rect()
                    getGlobalVisibleRect(sourceRectHint)
                    activity.setPictureInPictureParams(
                        PictureInPictureParams.Builder().setSourceRectHint(sourceRectHint)
                            .build()
                    )
                }
            }
        }
    }) {
        it.player = mediaController
        if (isInPip) {
            it.hideController()
        }
    }
}

private fun hideSystemBar(systemBarController: SystemUiController) {
    systemBarController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    systemBarController.isSystemBarsVisible = false

}

private fun showSystemBar(systemBarController: SystemUiController) {
    systemBarController.isSystemBarsVisible = true
}

@Composable
private fun MovieDetailBody(
    modifier: Modifier = Modifier,
    movie: MovieDetail,
    onPlaysSetClick: (PlaysSet) -> Unit,
    playInfo: PlayInfo?,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        MovieTitle(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(16.dp),
            name = movie.name,
            pic = movie.pic,
            actor = movie.actor,
            director = movie.director
        )
        MoviePlaySets(
            sets = movie.playSets,
            modifier = Modifier.fillMaxWidth(),
            playInfo = playInfo,
            onClick = onPlaysSetClick
        )
    }
}

@Composable
private fun MovieTitle(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    name: String,
    pic: String,
    actor: String,
    director: String,
) {
    Surface(modifier = modifier, tonalElevation = 8.dp, shape = MaterialTheme.shapes.large) {
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = pic,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxHeight()
                    .aspectRatio(3 / 4f, matchHeightConstraintsFirst = true),
                contentDescription = name
            )
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "导演: $director",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "演员: $actor",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
private fun MoviePlaySets(
    modifier: Modifier = Modifier,
    sets: List<PlaysSet>,
    playInfo: PlayInfo?,
    onClick: (PlaysSet) -> Unit,
) {
    val listState = rememberLazyListState()
    Surface(modifier = modifier, tonalElevation = 8.dp, shape = MaterialTheme.shapes.large) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sets) { set ->
                PlaySetItem(
                    playSet = set,
                    isSelected = set.mediaId == playInfo?.mediaController?.currentMediaItem?.mediaId,
                    onClick = onClick
                )
            }
        }
    }
    LaunchedEffect(key1 = playInfo) {
        val index = playInfo?.mediaController?.currentMediaItemIndex
        if (index != null && index >= 0) {
            listState.scrollToItem(index)
        }
    }
}

@Composable
fun PlaySetItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    playSet: PlaysSet,
    onClick: (PlaysSet) -> Unit = {},
) {

    Surface(
        modifier = modifier then Modifier.clickable {
            onClick(playSet)
        },
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        Text(
            text = playSet.name,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}