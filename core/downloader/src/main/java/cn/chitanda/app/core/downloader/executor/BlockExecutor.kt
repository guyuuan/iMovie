package cn.chitanda.app.core.downloader.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * @author: Chen
 * @createTime: 2023/6/2 15:05
 * @description:
 **/
class BlockExecutor<T, R>(
    capacity: Int, private val context: CoroutineScope, private val runnable: suspend (T) -> R
) {
    private val channel = Channel<Unit>(capacity)

    suspend fun execute(t: T): Job {
        channel.send(Unit)
        return with(context) {
            channel.runBlock {
                runnable(t)
            }
        }
    }
}

context (CoroutineScope)
inline fun Channel<*>.runBlock(crossinline block: suspend () -> Unit): Job {
    return launch {
        block()
    }.apply {
        invokeOnCompletion {
            launch { receive() }
        }
    }
}