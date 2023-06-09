package cn.chitanda.app.core.downloader.queue

import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import java.util.concurrent.PriorityBlockingQueue

/**
 * @author: Chen
 * @createTime: 2023/6/1 18:16
 * @description:
 **/
class M3u8DownloadTaskQueue private constructor(
    queue: PriorityBlockingQueue<M3u8DownloadTask>,
    override val taskQueueListener: M3u8TaskQueueListener?,
    private val autoRetry: Boolean,
    private val maxRetryCount: Int
) : DownloadTaskQueue<M3u8DownloadTask>(queue) {
    companion object {
        private var instance: M3u8DownloadTaskQueue? = null

        @JvmStatic
        operator fun invoke(
            taskQueueListener: M3u8TaskQueueListener? = null, autoRetry: Boolean, maxRetryCount: Int
        ): M3u8DownloadTaskQueue = instance ?: synchronized(this) {
            instance ?: M3u8DownloadTaskQueue(
                PriorityBlockingQueue(), taskQueueListener, autoRetry, maxRetryCount
            ).also {
                instance = it
            }
        }
    }

    private val taskExecuteRecord = mutableMapOf<String, Int>()
    override fun start() {
        forEachTask()
    }

    private fun forEachTask() {
        forEach(::checkTask)
    }

    override fun checkTask(task: M3u8DownloadTask) {
        when (task) {
            is M3u8DownloadTask.Completed -> {

            }

            is M3u8DownloadTask.Downloading -> {
                synchronized(taskExecuteRecord) {
                    taskExecuteRecord[task.id] = taskExecuteRecord.getOrPut(task.id) { 0 }.inc()
                }
            }

            is M3u8DownloadTask.Failed -> {
                synchronized(taskExecuteRecord) {
                    if (taskExecuteRecord.getOrPut(task.id) { 0 } < maxRetryCount && autoRetry) {
                        taskQueueListener?.executeTask(task)
                    }
                }
            }

            is M3u8DownloadTask.Initially -> {
                taskQueueListener?.onParse(task)
            }

            is M3u8DownloadTask.Parsed -> {
                taskQueueListener?.executeTask(task)
            }

            is M3u8DownloadTask.Paused -> TODO()
            is M3u8DownloadTask.Pending -> TODO()
        }
    }
}

interface M3u8TaskQueueListener : TaskQueueListener<M3u8DownloadTask> {
    fun onParse(task: M3u8DownloadTask.Initially)
}