package cn.chitanda.app.imovie.feature.play

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import cn.chitanda.app.imovie.core.data.repository.MoviesRepository
import cn.chitanda.app.imovie.core.media.AppMediaController
import cn.chitanda.app.imovie.core.media.MediaItemTree
import cn.chitanda.app.imovie.core.module.Movie
import cn.chitanda.app.imovie.core.module.PlaysSet
import cn.chitanda.app.imovie.core.module.asMovieDetail
import cn.chitanda.app.imovie.feature.play.navigation.PlayArgs
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
) : ViewModel() {
    private val playArgs = PlayArgs(savedStateHandle)

    private val _controller: MediaBrowser?
        get() = appMediaController.controller

    init {
        Log.w(TAG, "$TAG: init")
        appMediaController.viewModelInitialize { setController() }
        viewModelScope.launch {
            moviesRepository.getMovieDetail(playArgs.playId).map<Movie, PlayUiState> {
                val movie = it.asMovieDetail()
                mediaItemTree.initialize(movie)
                PlayUiState.Success(movie)
            }.catch {
                _playUiState.emit(PlayUiState.Failed(it))
            }.collectLatest {
                _playUiState.emit(it)
                setController()
            }

        }
    }

    private val _playUiState: MutableStateFlow<PlayUiState> =
        MutableStateFlow(PlayUiState.Loading())
    val playUiState: StateFlow<PlayUiState> get() = _playUiState

    private fun setController() {
        Log.d(TAG, "setController: $_controller")
        val controller = _controller ?: return
        _controller?.playWhenReady = true
        viewModelScope.launch {
            _playUiState.emit(_playUiState.value.update(playInfo = PlayInfo.Ideal(controller)))
        }
        val state = _playUiState.value
        viewModelScope.launch {
            _playUiState.emit(state.update(playInfo = if (controller.isPlaying) PlayInfo.Playing(
                controller) else PlayInfo.Pausing(controller)))
        }
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                viewModelScope.launch {
                    _playUiState.emit(state.update(playInfo = if (isPlaying) PlayInfo.Playing(
                        controller) else PlayInfo.Pausing(controller)))
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        viewModelScope.launch {
                            _playUiState.emit(state.update(playInfo = PlayInfo.Ideal(controller)))
                        }
                    }
                    Player.STATE_BUFFERING -> {
                        viewModelScope.launch {
                            _playUiState.emit(state.update(playInfo = PlayInfo.Buffering(controller)))
                        }
                    }
                    Player.STATE_READY -> {
                        viewModelScope.launch {
                            _playUiState.emit(state.update(playInfo = PlayInfo.Ready(controller)))
                        }
                    }
                    Player.STATE_ENDED -> {
                        viewModelScope.launch {
                            _playUiState.emit(state.update(playInfo = PlayInfo.Ending(controller)))
                        }
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "onPlayerError: $error")
                super.onPlayerError(error)
            }

        })
    }

    fun play(item: PlaysSet) {
        val controller = _controller ?: return
        val future = controller.getChildren(item.movieId.toString(), 0, Int.MAX_VALUE, null)
        future.addListener({
            val mediaList = future.get().value?.toList() ?: emptyList()
            controller.setMediaItems(mediaList, false)
            controller.seekToDefaultPosition(item.index)
            controller.prepare()
        }, MoreExecutors.directExecutor())
//        if (controller.mediaItemCount > item.index) {
//
//        }
    }

    fun setFullScreen(fullScreen: Boolean) {
        val state = playUiState.value
        viewModelScope.launch {
            _playUiState.emit(state.update(state.playInfo?.update(fullScreen = fullScreen)))
        }
    }

    override fun onCleared() {
        _controller?.apply {
            stop()
            setMediaItems(emptyList())
        }
        super.onCleared()
        appMediaController.removeViewModelListener()
    }


}

private const val TAG = "PlayScreenViewModel"