package cn.chitanda.app.imovie.feature.history

import androidx.paging.PagingData
import cn.chitanda.app.imovie.core.model.HistoryResource
import kotlinx.coroutines.flow.Flow

/**
 * @author: Chen
 * @createTime: 2023/2/10 17:09
 * @description:
 **/
data class HistoryUiState(
    val data: Flow<PagingData<HistoryResource>>,
)