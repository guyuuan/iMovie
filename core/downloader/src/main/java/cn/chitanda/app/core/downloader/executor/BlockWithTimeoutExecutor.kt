package cn.chitanda.app.core.downloader.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * @author: Chen
 * @createTime: 2023/6/2 15:05
 * @description:
 **/
class BlockWithTimeoutExecutor<T, R>(
    capacity: Int, private val scope: CoroutineScope, private val runnable: suspend (T) -> R
) {
    private val channel = Channel<Unit>(capacity)

    fun execute(t: T, timeout: (() -> T?)? = null): Job {
        return scope.launch {
            try {
                withTimeout(1000L) {
                    channel.send(Unit)
                }
                channel.runBlock {
                    runnable(t)
                }
            } catch (e: TimeoutCancellationException) {
                timeout?.invoke()
            }
        }
    }
}