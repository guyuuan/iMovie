package cn.chitanda.app.core.downloader.task

data class ActualDownloadTask(
    override val id: Int,
    override val originUrl: String,
    val parentTaskId: Int,
    val state: DownloadTaskState,
) : DownloadTask() {
    override val priority: Int = if (state.ordinal > 1) state.ordinal else 0
}
