package cn.chitanda.app.imovie.feature.recently

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.chitanda.app.imovie.core.data.repository.MoviesRepository
import cn.chitanda.app.imovie.core.module.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2022/11/20 12:41
 *@description:
 **/
private const val TAG = "HomeViewModel"

@HiltViewModel
class RecentlyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val moviesRepository: MoviesRepository,
) : ViewModel() {
    val homeState: StateFlow<RecentlyUiState> =
        moviesRepository.getHomePageData().map<HomeData, RecentlyUiState> { homeData ->
            RecentlyUiState.Shown(homeData)
        }.catch {
            emit(RecentlyUiState.LoadingFailed(it))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = RecentlyUiState.Loading
        )
}