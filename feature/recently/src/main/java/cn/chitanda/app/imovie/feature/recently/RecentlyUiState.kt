package cn.chitanda.app.imovie.feature.recently

import cn.chitanda.app.imovie.core.module.HomeData

/**
 *@author: Chen
 *@createTime: 2022/11/20 12:13
 *@description:
 **/
sealed interface RecentlyUiState {
    object Loading : RecentlyUiState
    data class LoadingFailed(val error: Throwable? = null) : RecentlyUiState
    data class Shown(val data: HomeData) : RecentlyUiState


}