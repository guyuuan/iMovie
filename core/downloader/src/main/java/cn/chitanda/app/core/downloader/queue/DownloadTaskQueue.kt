package cn.chitanda.app.core.downloader.queue

import cn.chitanda.app.core.downloader.task.DownloadTask
import java.util.Queue

/**
 * @author: Chen
 * @createTime: 2023/6/1 16:13
 * @description:
 **/
abstract class DownloadTaskQueue<T : DownloadTask>(queue: Queue<T>) : Queue<T> by queue {
    open val taskQueueListener: TaskQueueListener<T>? = null
    fun getTaskById(id: Any): T? {
        return find { it.id == id }
    }

    fun containsByUrl(url: String): Boolean {
        return find { it.originUrl == url } != null
    }

    open fun updateHead(updater: T.() -> T): Boolean {
        return poll()?.updater()?.let { new ->
            if (offer(new)) {
                checkTask(new)
                true
            } else {
                false
            }
        } ?: false
    }

    open fun updateById(id: Any, updater: T.() -> T): Boolean {
        val task = find { it.id == id }
        return if (task != null) {
            remove(task)
            val new = task.updater()
            if (offer(new)) {
                checkTask(new)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    abstract fun start()

    protected abstract fun checkTask(task: T)

}

interface TaskQueueListener<T : DownloadTask> {
    fun onComplete(task: T)

    fun onFailed(task: T)

    fun executeTask(task: T)
    fun retryTask(task: T)

}