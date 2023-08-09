@file:OptIn(ExperimentalMaterial3Api::class)

package cn.chitanda.app.imovie.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cn.chitanda.app.imovie.core.model.Movie
import cn.chitanda.app.imovie.ui.ext.plus
import cn.chitanda.app.imovie.ui.navigation.LocalNavigateToPlayScreen
import coil.compose.AsyncImage

/**
 * @author: Chen
 * @createTime: 2023/2/3 14:55
 * @description:
 **/
@Composable
fun SearchScreen(viewModel: SearchScreenViewModel = hiltViewModel()) {
    val searchResult = viewModel.data.collectAsLazyPagingItems()
    val navigationToPlay = LocalNavigateToPlayScreen.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            SearchInputBar(modifier = Modifier
                .padding(
                    PaddingValues(
                        horizontal = 24.dp,
                        vertical = 12.dp
                    ) + WindowInsets.statusBars.asPaddingValues()
                )
                .fillMaxWidth()
                .height(56.dp),
                value = viewModel.searchKey.value,
                onValueChange = {
                    viewModel.searchKey.value = it
                    searchResult.refresh()
                })
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = it + PaddingValues(vertical = 16.dp, horizontal = 12.dp)
        ) {
            items(key = searchResult.itemKey { m->m.id }, count = searchResult.itemCount) { index ->
                val movie = searchResult[index]
                if (movie != null) {
                    SearchResultItem(modifier = Modifier.clickable {
                        navigationToPlay(movie.id, false)
                    }, movie = movie)
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(modifier: Modifier = Modifier, movie: Movie) {
    Surface(
        modifier, tonalElevation = 4.dp, shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.pic,
                modifier = Modifier
                    .weight(2f)
                    .aspectRatio(ratio = 3 / 4f)
                    .clip(MaterialTheme.shapes.medium),
                contentDescription = movie.pic
            )
            Column(
                modifier = Modifier
                    .weight(6f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = movie.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = movie.director,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(text = movie.actor, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = movie.description,
                    maxLines = 3,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
//            TextButton(modifier = Modifier, onClick = {
//                navigationToPlay(history.movieId, true)
//            }) {
//                Text(text = stringResource(id = R.string.continue_playing))
//            }
        }
    }
}

@Composable
private fun SearchInputBar(
    modifier: Modifier = Modifier, value: String, onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight()
//        )
        Surface(modifier = modifier, shape = CircleShape, tonalElevation = 12.dp) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                  ,
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = onValueChange,
                    shape = CircleShape
                )
            }
        }
    }
}