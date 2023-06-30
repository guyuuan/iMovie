package cn.chitanda.app.core.downloader.queue

import cn.chitanda.app.core.downloader.task.DownloadTask
import cn.chitanda.app.core.downloader.usecase.UpdateTaskUseCase
import java.util.Queue

/**
 * @author: Chen
 * @createTime: 2023/6/1 16:13
 * @description:
 **/
internal abstract class DownloadTaskQueue<T : DownloadTask>(
    private val updateUseCase: UpdateTaskUseCase<T>, queue: Queue<T>
) : Queue<T> by queue {
    open val taskQueueListener: TaskQueueListener<T>? = null
    fun getTaskById(id: Any): T? {
        return find { it.id == id }
    }

    fun containsByUrl(url: String): Boolean {
        return find { it.originUrl == url } != null
    }

    open fun updateHead(check: Boolean = true, updater: T.() -> T): Boolean {
        return poll()?.updater()?.let { new ->
            offer(new).also {
                if (it) {
                    updateUseCase(new)
                    if (check) checkTask(new)
                }
            }
        } ?: false
    }

    open fun updateById(id: Any, check: Boolean = true, updater: T.() -> T): Boolean {
        val task = find { it.id == id }
        return if (task != null) {
            remove(task)
            val new = task.updater()
            offer(new).also {
                if (it) {
                    updateUseCase(new)
                    if (check) checkTask(new)
                }
            }

        } else {
            false
        }
    }

    abstract fun start()

    protected abstract fun checkTask(task: T)

    protected fun doNext() {
        peek()?.let {
            checkTask(it)
        }
    }

}

internal interface TaskQueueListener<T : DownloadTask> {
    fun onComplete(task: T)

    fun onFailed(task: T)

    fun onPause(task: T)
    fun onStart(task: T)
    fun onPending(task: T)

    fun onDownloading(task: T)
    fun retryTask(task: T)

}