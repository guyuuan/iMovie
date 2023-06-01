package cn.chitanda.app.core.downloader.queue

import cn.chitanda.app.core.downloader.task.DownloadTask
import java.util.Queue

/**
 * @author: Chen
 * @createTime: 2023/6/1 16:13
 * @description:
 **/
abstract class DownloadTaskQueue<T : DownloadTask>(queue: Queue<T>) :
    Queue<T> by queue {

    fun getTaskById(id: Int): DownloadTask? {
        return find { it.id == id }
    }

    fun containsByUrl(url: String): Boolean {
        return find { it.originUrl == url } != null
    }

    fun updateHead(block: T.() -> T) {
        poll()?.block()?.let {
            offer(it)
        }
    }

}