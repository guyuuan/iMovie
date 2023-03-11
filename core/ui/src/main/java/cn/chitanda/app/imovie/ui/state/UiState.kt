package cn.chitanda.app.imovie.ui.state

/**
 * @author: Chen
 * @createTime: 2023/3/9 15:14
 * @description:
 **/
sealed interface UiState {
    object Success : UiState{
        override fun toString() = "UiState.Success"
    }
    object Loading : UiState{
        override fun toString() = "UiState.Loading"
    }

    data class Error(val error: Throwable?) : UiState

}