@file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package cn.chitanda.app.imovie.feature.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.chitanda.app.imovie.core.model.HistoryResource
import cn.chitanda.app.imovie.ui.ext.plus
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import cn.chitanda.app.imovie.core.common.R.string as StringRes

/**
 * @author: Chen
 * @createTime: 2023/2/3 14:51
 * @description:
 **/

@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = hiltViewModel()) {
    val history = historyViewModel.data.collectAsLazyPagingItems()
    val snackbarState = remember {
        SnackbarHostState()
    }
    val snackbarMessage = stringResource(id = StringRes.you_have_deleted_history)
    val snackbarAction = stringResource(id = StringRes.revoke)
    val snackBarScope = rememberCoroutineScope()
    Scaffold(snackbarHost = {
        SnackbarHost(snackbarState)
    }) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = it + PaddingValues(vertical = 16.dp, horizontal = 12.dp)
        ) {
            items(key = { item ->
                item.movieId
            }, items = history) { hr ->
                hr?.let { h ->
                    HistoryItem(
                        history = h,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        onDeleted = { deleted ->
                            snackBarScope.launch {
                                historyViewModel.deleteHistory(deleted)
                                val result = snackbarState.showSnackbar(
                                    message = snackbarMessage.format(deleted.movieName),
                                    actionLabel = snackbarAction,
                                    withDismissAction = true
                                )
                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        historyViewModel.revokeDeleteHistory(deleted)
                                    }

                                    SnackbarResult.Dismissed -> {}
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun HistoryItem(
    history: HistoryResource,
    modifier: Modifier = Modifier,
    onDeleted: (HistoryResource) -> Unit,
) {
    val dismissState = rememberDismissState(confirmStateChange = {
        if (it == DismissValue.DismissedToStart) {
            onDeleted(history)
        }
        it != DismissValue.DismissedToStart
    })

    SwipeToDismiss(state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = {
            FractionalThreshold(
                if (it == DismissDirection.EndToStart) 0f else 0.05f
            )
        },
        background = {
            val color by animateColorAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) Color.Transparent else MaterialTheme.colorScheme.error)
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.5f else 1f
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 5.dp), contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Delete Icon",
                    modifier = Modifier
                        .scale(scale),
                    tint = color
                )
            }
        }) {
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
                    model = history.moviePic,
                    modifier = Modifier
                        .weight(2f)
                        .aspectRatio(ratio = 3 / 4f)
                        .clip(MaterialTheme.shapes.medium),
                    contentDescription = history.moviePic
                )
                Column(
                    modifier = Modifier
                        .weight(6f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = history.movieName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${stringResource(id = StringRes.last_seen)} ${history.indexName} ${history.lastSeen}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val progress = history.position / history.duration.toFloat()
                        LinearProgressIndicator(progress, modifier = Modifier.weight(1f))
                        Text(
                            text = "${(progress * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                TextButton(modifier = Modifier, onClick = {}){
                    Text(text = stringResource(id = StringRes.continue_playing))
                }
            }
        }
    }

}

@Composable
private fun HistoryScreenSnackBar(state: SnackbarHostState) {
    SnackbarHost(state) { data ->
        (data.visuals as? RevokeDeleteSnackVisuals)?.let { _ ->
        }
    }
}

private class RevokeDeleteSnackVisuals(
    val data: HistoryResource,
    messageHeader: String,
    override val actionLabel: String
) : SnackbarVisuals {
    override val duration = SnackbarDuration.Short
    override val message = messageHeader.format(data.movieName)
    override val withDismissAction = true
}