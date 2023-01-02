@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)

package cn.chitanda.app.imovie.feature.home

import androidx.annotation.IntRange
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import cn.chitanda.app.imovie.core.design.windowsize.LocalWindowSizeClass
import cn.chitanda.app.imovie.core.module.Movie
import cn.chitanda.app.imovie.feature.home.navigation.NavigationToPlay
import coil.compose.AsyncImage

/**
 *@author: Chen
 *@createTime: 2022/11/20 12:41
 *@description:
 **/
private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel(), navigationToPlay: (Long) -> Unit) {
    val homeState by homeViewModel.homeState.collectAsState()
    val isLandSpace = LocalWindowSizeClass.current.widthSizeClass > WindowWidthSizeClass.Compact
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "iMovie")
        })
    }) {
        when (homeState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.LoadingFailed -> {
                Box(modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    Text(text = (homeState as HomeUiState.LoadingFailed).error.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error)
                }
            }
            is HomeUiState.Shown -> {
                val data = (homeState as HomeUiState.Shown).data
                if (isLandSpace) {
                    LazyRow(contentPadding = it + PaddingValues(vertical = 12.dp,
                        horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        items(3, contentType = {
                            "card"
                        }) { i ->
                            MoviesCard(modifier = Modifier.width(600.dp),
                                isLandSpace = true,
                                data = data[i],
                                type = i + 1,
                                onItemClick = navigationToPlay)
                        }
                    }
                } else {
                    LazyColumn(contentPadding = it + PaddingValues(vertical = 12.dp,
                        horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        items(3, contentType = {
                            "card"
                        }) { i ->
                            MoviesCard(modifier = Modifier.fillMaxWidth(),
                                data = data[i],
                                isLandSpace = false,
                                type = i + 1,
                                onItemClick = navigationToPlay)
                        }
                    }
                }

            }
        }
    }
}

@Composable
private operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    val ld = LocalLayoutDirection.current
    return PaddingValues(top = this.calculateTopPadding() + paddingValues.calculateTopPadding(),
        bottom = this.calculateBottomPadding() + paddingValues.calculateBottomPadding(),
        start = this.calculateStartPadding(ld) + paddingValues.calculateStartPadding(ld),
        end = this.calculateEndPadding(ld) + paddingValues.calculateEndPadding(ld))
}

@Composable
private fun MoviesCard(
    modifier: Modifier = Modifier, data: List<Movie>, @IntRange(from = 1, to = 3) type: Int,
    onItemClick: NavigationToPlay,
    isLandSpace: Boolean,
) {
    val split = data.size / 2
    val typeString = when (type) {
        1 -> "电影"
        2 -> "动漫"
        else -> "电视剧"
    }
    Surface(modifier = modifier, shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
        Column(modifier = Modifier
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = typeString, style = MaterialTheme.typography.displaySmall)
            repeat(2) {
                val start = it * split
                val list = data.subList(start, start + split)
                Row(modifier = Modifier
                    .fillMaxWidth()
                     then if(isLandSpace) Modifier .weight(1f) else Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (movie in list) {
                        MovieItem(movie = movie,
                            isLandSpace = isLandSpace,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = onItemClick)
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: Movie,
    onClick: NavigationToPlay,
    isLandSpace: Boolean,
) {
    if (isLandSpace) {
        Row(modifier = modifier.clickable {
            onClick(movie.id)
        },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            AsyncImage(model = movie.pic,
                contentDescription = movie.name,
                contentScale = ContentScale.Crop,
                modifier =
                Modifier
                    .weight(10f)
                    .aspectRatio(ratio = 3 / 4f, matchHeightConstraintsFirst = true)
                    .clip(MaterialTheme.shapes.small))
            Spacer(modifier = Modifier.width(1.dp))
            Text(
                text = movie.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f),
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        Column(modifier = modifier.clickable {
            onClick(movie.id)
        },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(model = movie.pic,
                contentDescription = movie.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 3 / 4f)
                    .clip(MaterialTheme.shapes.small))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = movie.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}