package cn.chitanda.app.core.downloader.queue

import cn.chitanda.app.core.downloader.task.ActualDownloadTask
import cn.chitanda.app.core.downloader.task.DownloadTaskState
import java.util.concurrent.PriorityBlockingQueue

/**
 * @author: Chen
 * @createTime: 2023/6/5 09:25
 * @description:
 **/
class ActualDownloadTaskQueue private constructor(
    queue: PriorityBlockingQueue<ActualDownloadTask>,
    override val taskQueueListener: ActualTaskQueueListener?,
    private val autoRetry: Boolean,
    private val maxRetryCount: Int
) : DownloadTaskQueue<ActualDownloadTask>(queue) {
    companion object {
        private var instance: ActualDownloadTaskQueue? = null

        @JvmStatic
        operator fun invoke(
            taskQueueListener: ActualTaskQueueListener? = null,
            autoRetry: Boolean,
            maxRetryCount: Int
        ): ActualDownloadTaskQueue = instance ?: synchronized(this) {
            instance ?: ActualDownloadTaskQueue(
                PriorityBlockingQueue(), taskQueueListener, autoRetry, maxRetryCount
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
            DownloadTaskState.completed -> {
                remove(task)
                taskExecuteRecord.remove(task.id)
                taskQueueListener?.onComplete(task)
            }

            DownloadTaskState.failed -> {
                synchronized(taskExecuteRecord) {
                    if (taskExecuteRecord.getOrPut(task.id) { 0 } < maxRetryCount && autoRetry) {
                        taskQueueListener?.retryTask(task)
                    }else{
                    }
                }
            }

            DownloadTaskState.initially, DownloadTaskState.pending -> {
                taskQueueListener?.executeTask(task)
            }

            DownloadTaskState.pause -> {}
            DownloadTaskState.downloading -> {
                synchronized(taskExecuteRecord) {
                    taskExecuteRecord[task.id] = taskExecuteRecord.getOrPut(
                        task.id
                    ) { 0 }.inc()
                }
            }

            else -> {}
        }
    }


    override fun start() {
        forEachTask()
    }
}

interface ActualTaskQueueListener : TaskQueueListener<ActualDownloadTask> {
}