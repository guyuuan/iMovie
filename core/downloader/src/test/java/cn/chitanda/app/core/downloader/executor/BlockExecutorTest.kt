package cn.chitanda.app.core.downloader.executor

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import org.junit.Test


/**
 * @author: Chen
 * @createTime: 2023/6/2 15:59
 * @description:
 */
class BlockExecutorTest {


    @Test
    fun executor_run_when_block_throw_exception(): Unit =
        runBlocking {
            val executor = BlockExecutor(
                2,
                CoroutineScope(this.coroutineContext + SupervisorJob() + CoroutineExceptionHandler { _, t ->
                    println(t)
                })
            ) { b: Int ->
                delay(2000)
                println(1 / b)
            }

            listOf(
                executor.execute(1),
                executor.execute(0),
                executor.execute(2)
            ).joinAll()
        }
}