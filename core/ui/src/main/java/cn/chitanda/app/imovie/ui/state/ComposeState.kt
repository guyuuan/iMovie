package cn.chitanda.app.imovie.ui.state

/**
 * @author: Chen
 * @createTime: 2023/9/4 15:01
 * @description:
 **/
abstract class ComposeUiState<Event : ComposeUiEvent> {

    abstract val onCollect: (Event) -> Unit
    fun emit(event: Event) {
        onCollect(event)
    }
}

open class ComposeUiEvent