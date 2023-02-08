package cn.chitanda.app.imovie.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import cn.chitanda.app.imovie.core.data.repository.HistoryRepository
import cn.chitanda.app.imovie.core.data.repository.asHistoryResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 2023/2/8 15:19
 * @description:
 **/
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val pager = Pager(
        config = PagingConfig(pageSize = 20), pagingSourceFactory = ::pagingSourceFactory
    )

    val data = pager.flow.map { it.map { h -> h.asHistoryResource() } }.cachedIn(viewModelScope)

    var searchQuery: String = ""
    private fun pagingSourceFactory() =
        if (searchQuery.isNotEmpty() && searchQuery.isNotBlank()) {
            historyRepository.getSearchHistoryPagingSource(
                searchQuery
            )
        } else {
            historyRepository.getHistoryPagingSource()
        }
}