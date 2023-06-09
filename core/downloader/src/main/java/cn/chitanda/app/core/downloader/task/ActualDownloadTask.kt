package cn.chitanda.app.core.downloader.task

import cn.chitanda.app.core.downloader.db.entites.ActualTaskEntity
import cn.chitanda.app.core.downloader.utils.nowMilliseconds

data class ActualDownloadTask(
    override val id: String,
    override val originUrl: String,
    val parentTaskId: String,
    val state: DownloadTaskState,
    val fileName: String,
    val downloadDir: String,
    override val createTime: Long = nowMilliseconds(),
    override val updateTime: Long = createTime,
    val error: Throwable? = null,
) : DownloadTask() {
    override val priority: Int = if (state.ordinal > 1) state.ordinal else 0
}

fun ActualTaskEntity.toActualDownloadTask(): ActualDownloadTask {
    return ActualDownloadTask(
        id = id.toString(),
        originUrl = originUrl,
        parentTaskId = parentTaskId,
        state = state,
        fileName = fileName,
        downloadDir = downloadDir,
        createTime = createTime,
        updateTime = updateTime
    )
}

fun ActualDownloadTask.toActualTaskEntity(): ActualTaskEntity {
    return ActualTaskEntity(
        id = id.toIntOrNull() ?: 0,
        originUrl = originUrl,
        parentTaskId = parentTaskId,
        state = state,
        fileName = fileName,
        downloadDir = downloadDir,
        createTime = createTime,
        updateTime = updateTime
    )
}