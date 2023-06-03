package cn.chitanda.app.core.downloader.task

import kotlinx.datetime.Clock

data class ActualDownloadTask(
    override val id: Int,
    override val originUrl: String,
    val parentTaskId: Int,
    val state: DownloadTaskState,
    override val createTime: Long=Clock.System.now().toEpochMilliseconds(),
    override val updateTime: Long = createTime,
) : DownloadTask() {
    override val priority: Int = if (state.ordinal > 1) state.ordinal else 0
}
