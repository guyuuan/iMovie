package cn.chitanda.app.imovie.feature.home

import cn.chitanda.app.imovie.core.module.HomeData

/**
 *@author: Chen
 *@createTime: 2022/11/20 12:13
 *@description:
 **/
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class LoadingFailed(val error: Throwable? = null) : HomeUiState
    data class Shown(val data: HomeData) : HomeUiState


}