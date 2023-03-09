package cn.chitanda.app.imovie.ui.ext

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty1

/**
 *@author: Chen
 *@createTime: 2022/12/7 11:46
 *@description:
 **/
suspend inline fun <T> MutableStateFlow<T>.setStat(reducer: T.() -> T) {
    this.emit(this.value.reducer())
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun <T, A> StateFlow<T>.collectPartAsState(
    context: CoroutineContext = EmptyCoroutineContext, part: KProperty1<T, A>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): State<A> =
    produceState(part.get(this.value), this, context) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                map { StateTuple1(part.get(it)) }.distinctUntilChanged().collect {
                    value = it.a
                }
            } else withContext(context) {
                map { StateTuple1(part.get(it)) }.distinctUntilChanged().collect {
                    value = it.a
                }
            }
        }
    }

internal data class StateTuple1<A>(val a: A)