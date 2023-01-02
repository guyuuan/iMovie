@file:OptIn(ExperimentalLifecycleComposeApi::class)

package cn.chitanda.app.imovie.feature.play

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import cn.chitanda.app.imovie.core.design.windowsize.LocalWindowSizeClass
import cn.chitanda.app.imovie.core.module.MovieDetail
import cn.chitanda.app.imovie.core.module.PlaysSet
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 *@author: Chen
 *@createTime: 2022/11/22 16:51
 *@description:
 **/
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun PlayScreen(viewModel: PlayScreenViewModel = hiltViewModel()) {
    val uiState by viewModel.playUiState.collectAsState()
    val playInfo = uiState.playInfo
    val sizeClass = LocalWindowSizeClass.current.widthSizeClass
    val fullScreen = uiState.playInfo?.fullScreen ?: false
    Surface {
        when {
            sizeClass > WindowWidthSizeClass.Compact -> {
                Row(modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    VideoView(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        playInfo = playInfo,
                        fullScreen = fullScreen,
                    )
//                    AnimatedVisibility(visible = !fullScreen) {
//                        Surface(modifier = Modifier
//                            .fillMaxHeight()
//                            .weight(1f)) {
//                            when (uiState) {
//                                is PlayUiState.Success -> {
//                                    MovieDetailBody(modifier = Modifier
//                                        .windowInsetsPadding(WindowInsets.statusBars)
//                                        .padding(16.dp)
//                                        .fillMaxSize(),
//                                        playInfo = playInfo,
//                                        onPlaysSetClick = {
//                                            viewModel.play(it)
//                                        },
//                                        movie = (uiState as PlayUiState.Success).movie)
//                                }
//                                is PlayUiState.Failed -> {
//                                    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
//                                        Text(text = (uiState as PlayUiState.Failed).error.toString(),
//                                            style = MaterialTheme.typography.bodyLarge,
//                                            color = MaterialTheme.colorScheme.error)
//                                    }
//                                }
//                                is PlayUiState.Loading -> {
//                                    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
//                                        CircularProgressIndicator()
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    VideoView(
                        modifier = Modifier.fillMaxWidth(),
                        viewModel = viewModel,
                        playInfo = playInfo,
                        fullScreen = fullScreen,
                    )
                    AnimatedVisibility(visible = !fullScreen) {
                        Surface(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)) {
                            when (uiState) {
                                is PlayUiState.Success -> {
                                    MovieDetailBody(modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxSize(), playInfo = playInfo, onPlaysSetClick = {
                                        viewModel.play(it)
                                    }, movie = (uiState as PlayUiState.Success).movie)
                                }
                                is PlayUiState.Failed -> {
                                    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                                        Text(text = (uiState as PlayUiState.Failed).error.toString(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.error)
                                    }
                                }
                                is PlayUiState.Loading -> {
                                    Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private const val TAG = "PlayScreen"

@SuppressLint("SourceLockedOrientationActivity")
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoView(
    playInfo: PlayInfo?,
    viewModel: PlayScreenViewModel,
    modifier: Modifier = Modifier,
    fullScreen: Boolean,
) {
//    val isLandSpace = LocalWindowSizeClass.current.widthSizeClass > WindowWidthSizeClass.Compact
    val systemBarController = rememberSystemUiController()
    fun hideSystemBar() {
        systemBarController.isSystemBarsVisible = false
        systemBarController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    fun showSystemBar() {
        systemBarController.isSystemBarsVisible = true
    }

    val activity = LocalContext.current as Activity
    Surface(color = Color.Black) {
        AndroidView(modifier = if (fullScreen) {
            Modifier.fillMaxSize()
        } else {
            modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .aspectRatio(16 / 9f)
        }, factory = {
            PlayerView(it).apply {
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)
                setFullscreenButtonClickListener { fullScreen ->
                    viewModel.setFullScreen(fullScreen && activity.requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                }
            }
        }) {
            it.player = playInfo?.mediaController
        }
    }
    DisposableEffect(key1 = fullScreen) {
        Log.d(TAG, "VideoView: full screen = $fullScreen")
        if (fullScreen) {
            hideSystemBar()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            showSystemBar()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        onDispose {
            Log.d(TAG, "VideoView: onDispose  full screen = $fullScreen")
            showSystemBar()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }
}

@Composable
private fun MovieDetailBody(
    modifier: Modifier = Modifier,
    movie: MovieDetail,
    onPlaysSetClick: (PlaysSet) -> Unit,
    playInfo: PlayInfo?,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        MovieTitle(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
            contentPadding = PaddingValues(16.dp),
            name = movie.name,
            pic = movie.pic,
            actor = movie.actor,
            director = movie.director)
        MoviePlaySets(sets = movie.playSets,
            modifier = Modifier.fillMaxWidth(),
            playInfo = playInfo,
            onClick = onPlaysSetClick)
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
        Row(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AsyncImage(model = pic,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxHeight()
                    .aspectRatio(3 / 4f, matchHeightConstraintsFirst = true),
                contentDescription = name)
            Column(modifier = Modifier
                .padding(2.dp)
                .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
                Text(text = "导演: $director",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis)
                Text(text = "演员: $actor",
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis)
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
    Surface(modifier = modifier, tonalElevation = 8.dp, shape = MaterialTheme.shapes.large) {
        LazyRow(modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(sets) { set ->
                PlaySetItem(playSet = set,
                    isSelected = set.mediaId == playInfo?.mediaController?.currentMediaItem?.mediaId && playInfo is PlayInfo.Playing,
                    onClick = onClick)
            }
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

    Surface(modifier = modifier then Modifier.clickable {
        onClick(playSet)
    },
        shape = CircleShape,
        color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp) {
        Text(text = playSet.name,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium)
    }
}