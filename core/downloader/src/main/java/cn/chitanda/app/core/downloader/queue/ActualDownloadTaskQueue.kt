package cn.chitanda.app.core.downloader.queue

import cn.chitanda.app.core.downloader.task.ActualDownloadTask
import cn.chitanda.app.core.downloader.task.DownloadTaskState
import cn.chitanda.app.core.downloader.usecase.UpdateTaskUseCase
import java.util.concurrent.PriorityBlockingQueue

/**
 * @author: Chen
 * @createTime: 2023/6/5 09:25
 * @description:
 **/
internal class ActualDownloadTaskQueue private constructor(
    queue: PriorityBlockingQueue<ActualDownloadTask>,
    override val taskQueueListener: ActualTaskQueueListener,
    private val autoRetry: Boolean,
    private val maxRetryCount: Int,
    useCase: UpdateTaskUseCase<ActualDownloadTask>
) : DownloadTaskQueue<ActualDownloadTask>(useCase, queue) {
    companion object {
        private var instance: ActualDownloadTaskQueue? = null

        @JvmStatic
        operator fun invoke(
            taskQueueListener: ActualTaskQueueListener,
            autoRetry: Boolean,
            maxRetryCount: Int,
            useCase: UpdateTaskUseCase<ActualDownloadTask>
        ): ActualDownloadTaskQueue = instance ?: synchronized(this) {
            instance ?: ActualDownloadTaskQueue(
                PriorityBlockingQueue(), taskQueueListener, autoRetry,
                maxRetryCount, useCase
            ).also {
                instance = it
            }
        }
    }

    private val taskExecuteRecord = mutableMapOf<String, Int>()
    private fun forEachTask() {
        forEach(::checkTask)
    }

    override fun checkTask(task: ActualDownloadTask) {
        when (task.state) {
            DownloadTaskState.Completed -> {
                remove(task)
                taskExecuteRecord.remove(task.id)
                taskQueueListener.onComplete(task)
                doNext()
            }

            DownloadTaskState.Failed -> {
                synchronized(taskExecuteRecord) {
                    if (taskExecuteRecord.getOrPut(task.id) { 0 } < maxRetryCount && autoRetry) {
                        taskQueueListener.retryTask(task)
                    }
                }
                doNext()
            }

            DownloadTaskState.Initially, DownloadTaskState.Pending -> {
                taskQueueListener.onDownloading(task)
            }

            DownloadTaskState.Pause -> {
                taskQueueListener.onPause(task)
            }

            DownloadTaskState.Downloading -> {
                synchronized(taskExecuteRecord) {
                    taskExecuteRecord[task.id] = taskExecuteRecord.getOrPut(
                        task.id
                    ) { 0 }.inc().coerceAtLeast(0)
                }
            }

            else -> {}
        }
    }


    override fun start() {
        forEachTask()
    }
}

internal interface ActualTaskQueueListener : TaskQueueListener<ActualDownloadTask> {
    override fun onPause(task: ActualDownloadTask) {
    }

    override fun onStart(task: ActualDownloadTask) {
    }

    override fun onPending(task: ActualDownloadTask) {
    }

}