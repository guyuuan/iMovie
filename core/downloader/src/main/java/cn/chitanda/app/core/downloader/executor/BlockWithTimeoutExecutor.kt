package cn.chitanda.app.core.downloader.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout

/**
 * @author: Chen
 * @createTime: 2023/6/2 15:05
 * @description:
 **/
class BlockWithTimeoutExecutor<T, R>(
    capacity: Int, private val context: CoroutineScope, private val runnable: suspend (T) -> R
) {
    private val channel = Channel<Unit>(capacity)

    suspend fun execute(t: T, timeout: (() -> T?)? = null): Job? {
        return try {
            withTimeout(1000L) {
                channel.send(Unit)
            }
            with(context) {
                channel.runBlock {
                    runnable(t)
                }
            }
        } catch (e: TimeoutCancellationException) {
            val t = timeout?.invoke()
            t?.let {
                channel.send(Unit)
                with(context) {
                    channel.runBlock {
                        runnable(it)
                    }
                }
            }
        }
    }
}