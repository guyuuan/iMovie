package cn.chitanda.app.core.downloader.task

/**
 * @author: Chen
 * @createTime: 2023/6/1 15:15
 * @description:
 **/
abstract class DownloadTask : Comparable<DownloadTask> {
    abstract val id: Int
    abstract val originUrl: String
    abstract val priority: Int
    abstract val createTime:Long
    abstract val updateTime:Long
    override fun compareTo(other: DownloadTask): Int {
        return when {
            priority > other.priority -> 1
            priority < other.priority -> -1
            else -> 0
        }
    }
}
