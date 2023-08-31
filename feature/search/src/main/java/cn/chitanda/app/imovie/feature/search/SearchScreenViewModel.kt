package cn.chitanda.app.imovie.feature.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.chitanda.app.imovie.core.data.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 2023/2/15 15:27
 * @description:
 **/
@HiltViewModel
class SearchScreenViewModel @Inject constructor(private val moviesRepository: MoviesRepository) :
    ViewModel() {
    val data = Pager(
        config = PagingConfig(pageSize = 10), pagingSourceFactory = {
            SearchResultPagingSource(moviesRepository, searchKey.value)
        }
    ).flow.cachedIn(viewModelScope)

    var searchKey = mutableStateOf("")
}