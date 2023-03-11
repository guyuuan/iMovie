package cn.chitanda.app.imovie.feature.play

import android.media.session.PlaybackState
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import cn.chitanda.app.imovie.core.data.repository.HistoryRepository
import cn.chitanda.app.imovie.core.data.repository.MoviesRepository
import cn.chitanda.app.imovie.core.ext.safeLaunch
import cn.chitanda.app.imovie.core.media.AppMediaController
import cn.chitanda.app.imovie.core.media.MediaItemTree
import cn.chitanda.app.imovie.core.model.HistoryResource
import cn.chitanda.app.imovie.core.model.Movie
import cn.chitanda.app.imovie.core.model.PlaysSet
import cn.chitanda.app.imovie.core.model.asMovieDetail
import cn.chitanda.app.imovie.feature.play.navigation.PlayArgs
import cn.chitanda.app.imovie.ui.ext.setState
import cn.chitanda.app.imovie.ui.state.UiState
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:39
 *@description:
 **/
@HiltViewModel
class PlayScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moviesRepository: MoviesRepository,
    private val appMediaController: AppMediaController,
    private val mediaItemTree: MediaItemTree,
    private val historyRepository: HistoryRepository,
) : ViewModel(), Player.Listener {
    private var hasPlayed = false

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        hasPlayed = true
        val controller = _controller ?: return
        Log.d(TAG, "onIsPlayingChanged: $isPlaying")
        safeLaunch {
            val history = updateHistory()
            _playUiState.setState {
                copy(
                    playInfo = if (isPlaying) {
                        PlayInfo.Playing(
                            controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                        )
                    } else {
                        PlayInfo.Pausing(
                            controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                        )
                    }, history = history
                )
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        val controller = _controller ?: return
        when (playbackState) {
            Player.STATE_IDLE -> {
                safeLaunch {
                    _playUiState.setState {
                        copy(
                            playInfo = PlayInfo.Ideal(
                                controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                            )
                        )
                    }

                }
            }

            Player.STATE_BUFFERING -> {
                safeLaunch {
                    _playUiState.setState {
                        copy(
                            playInfo = PlayInfo.Buffering(
                                controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                            )
                        )
                    }
                }
            }

            Player.STATE_READY -> {
                safeLaunch {
                    _playUiState.setState {
                        copy(
                            playInfo = PlayInfo.Ready(
                                controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                            )
                        )
                    }
                }
            }

            Player.STATE_ENDED -> {
                safeLaunch {
                    _playUiState.setState {
                        copy(
                            playInfo = PlayInfo.Ending(
                                controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                            )
                        )
                    }
                }
            }

        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e(TAG, "onPlayerError: $error")
        super.onPlayerError(error)
    }

    private val playArgs = PlayArgs(savedStateHandle)

    private val _controller: MediaBrowser?
        get() = appMediaController.controller

    init {
        Log.w(TAG, "$TAG: init")
        appMediaController.viewModelInitialize { setController(null) }
        safeLaunch {
            moviesRepository.getMovieDetail(playArgs.playId).map<Movie, PlayUiState> {
                withContext(Dispatchers.IO) {
                    val movie = it.asMovieDetail()
                    mediaItemTree.initialize(movie)
                    PlayUiState(
                        state = UiState.Success,
                        history = historyRepository.findHistoryById(movie.id),
                        movie = movie,
                    )
                }
            }.catch {
                it.printStackTrace()
                _playUiState.setState {
                    copy(state = UiState.Error(it))
                }
            }.collectLatest {
                _playUiState.emit(it)
                withContext(Dispatchers.Main) { setController(it.movie?.id.toString()) }
            }

        }
    }

    private val _playUiState: MutableStateFlow<PlayUiState> =
        MutableStateFlow(PlayUiState(state = UiState.Loading))
    val playUiState: StateFlow<PlayUiState> get() = _playUiState

    private val uiState get() = _playUiState.value
    private fun setController(movieId: String?) {
        val controller = _controller ?: return
        controller.playWhenReady = playArgs.playFromHistory
        safeLaunch {
            _playUiState.setState {
                copy(
                    playInfo = if (controller.isPlaying) {
                        PlayInfo.Playing(
                            controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                        )
                    } else if (controller.playbackState == PlaybackState.STATE_NONE) {
                        PlayInfo.Ideal(
                            controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                        )
                    } else {
                        PlayInfo.Pausing(
                            controller, fullScreen = uiState.playInfo?.fullScreen ?: false
                        )
                    }
                )
            }
        }
        controller.addListener(this)
        val future = movieId?.let { controller.getChildren(it, 0, Int.MAX_VALUE, null) }
        future?.addListener({
            val mediaList = future.get().value?.toList() ?: emptyList()
            controller.setMediaItems(mediaList, false)
            if (playArgs.playFromHistory) {
                val current = uiState
                val state = current.state
                if (state is UiState.Success && current.history != null) {
                    controller.seekTo(current.history.index, current.history.position)
                }
            }
            controller.prepare()
        }, MoreExecutors.directExecutor())
    }

    fun play(item: PlaysSet) {
        val controller = _controller ?: return
        controller.seekToDefaultPosition(item.index)
        controller.prepare()
        controller.play()
    }

    fun playFromHistory(history: HistoryResource) {
        val controller = _controller ?: return
        controller.seekTo(history.index, history.position)
        controller.prepare()
        controller.play()
    }

    suspend fun updateHistory(): HistoryResource? = withContext(Dispatchers.IO) {
        if (!hasPlayed) return@withContext null
        val controller = _controller ?: return@withContext null
        if (withContext(Dispatchers.Main) { controller.currentMediaItemIndex } < 0) return@withContext null
        var update = true
        var movieId: Long? = null
        val history = withContext(Dispatchers.Main) {
            val current = uiState
            (if (current.state == UiState.Success) uiState else null)?.let {
                val movieDetail = it.movie ?: return@let null
                movieId = movieDetail.id
                it.history?.copy(
                    movieId = movieDetail.id,
                    movieName = movieDetail.name,
                    moviePic = movieDetail.pic,
                    updateTime = System.currentTimeMillis(),
                    duration = controller.contentDuration,
                    position = controller.currentPosition,
                    index = controller.currentMediaItemIndex,
                    indexName = movieDetail.playSets[controller.currentMediaItemIndex].name,
                ) ?: (HistoryResource(
                    movieId = movieDetail.id,
                    movieName = movieDetail.name,
                    moviePic = movieDetail.pic,
                    updateTime = System.currentTimeMillis(),
                    duration = controller.contentDuration,
                    position = controller.currentPosition,
                    index = controller.currentMediaItemIndex,
                    indexName = movieDetail.playSets[controller.currentMediaItemIndex].name,
                ).also { update = false })
            }
        }
        Log.d(TAG, "updateHistory: $history")
        if (history != null) {
            if (update) {
                historyRepository.updateHistory(history)
            } else {
                historyRepository.insertHistory(history)
            }
        }
        if (movieId != null) historyRepository.findHistoryById(movieId!!) else null
    }

    fun changeFullScreenState() {
        val state = playUiState.value
        val fullScreen = state.playInfo?.fullScreen ?: false
        safeLaunch {
            _playUiState.setState {
                copy(playInfo = playInfo?.update(fullScreen = !fullScreen))
            }
        }
    }

    fun handleOnBackPressed(): Boolean {
        val state = playUiState.value
        return state.playInfo?.fullScreen ?: false
    }

    override fun onCleared() {
        _controller?.apply {
            stop()
            setMediaItems(emptyList())
            removeListener(this@PlayScreenViewModel)
        }
        super.onCleared()
        appMediaController.removeViewModelListener()
    }


}

private const val TAG = "PlayScreenViewModel"