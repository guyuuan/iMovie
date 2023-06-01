package cn.chitanda.app.core.downloader.queue

import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import java.util.concurrent.PriorityBlockingQueue

/**
 * @author: Chen
 * @createTime: 2023/6/1 18:16
 * @description:
 **/
class M3u8DownloadTaskQueue private constructor(queue: PriorityBlockingQueue<M3u8DownloadTask>) :
    DownloadTaskQueue<M3u8DownloadTask>(queue) {
    companion object {
        private var instance: M3u8DownloadTaskQueue? = null

        @JvmStatic
        operator fun invoke(): M3u8DownloadTaskQueue = instance ?: synchronized(this) {
            instance ?: M3u8DownloadTaskQueue(PriorityBlockingQueue()).also {
                instance = it
            }
        }
    }
}