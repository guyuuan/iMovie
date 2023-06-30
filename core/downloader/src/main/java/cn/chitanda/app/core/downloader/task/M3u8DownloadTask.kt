package cn.chitanda.app.core.downloader.task

import cn.chitanda.app.core.downloader.db.entites.M3u8TaskEntity
import cn.chitanda.app.core.downloader.m3u8.M3u8Data
import cn.chitanda.app.core.downloader.utils.nowMilliseconds

/**
 * @author: Chen
 * @createTime: 2023/5/31 14:33
 * @description:
 **/
sealed class M3u8DownloadTask(
    override val originUrl: String,
    override val id: String,
    val coverImageUrl: String?,
    override val priority: Int = 0,
    override val createTime: Long,
    override val updateTime: Long,
    override val savePath: String
) : DownloadTask(), TaskStateTransform<ActualDownloadTask> {

    class Initially(
        originUrl: String,
        taskId: String,
        coverImageUrl: String?,
        createTime: Long = nowMilliseconds(),
        updateTime: Long = createTime,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        createTime = createTime,
        updateTime = updateTime,
        savePath = savePath
    ) {
        override fun parse(m3u8Data: M3u8Data): Parsed {
            return Parsed(originUrl, id, m3u8Data, coverImageUrl, createTime, updateTime, savePath)
        }
    }


    class Parsed(
        originUrl: String,
        taskId: String,
        val m3u8Data: M3u8Data,
        coverImageUrl: String?,
        createTime: Long,
        updateTime: Long,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.Initially.ordinal,
        createTime,
        updateTime,
        savePath
    ) {
        override fun start(): Downloading {
            return Downloading(
                originUrl,
                id,
                coverImageUrl,
                m3u8Data,
                createTime = createTime,
                updateTime = nowMilliseconds(),
                savePath
            )
        }

        override fun pending(): Pending = Pending(
            originUrl,
            id,
            m3u8Data,
            coverImageUrl,
            createTime = createTime,
            updateTime = nowMilliseconds(),
            savePath
        )
    }

    class Pending(
        originUrl: String,
        taskId: String,
        val m3u8Data: M3u8Data,
        coverImageUrl: String?,
        createTime: Long,
        updateTime: Long,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        DownloadTaskState.Pending.ordinal,
        createTime,
        updateTime,
        savePath
    ) {
        override fun start(): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds(), savePath
            )
        }
    }

    class Downloading(
        originUrl: String,
        taskId: String,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        createTime: Long,
        updateTime: Long,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        DownloadTaskState.Downloading.ordinal,
        createTime,
        updateTime,
        savePath
    ) {
        override fun pause(): Paused {
            return Paused(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds(), savePath
            )
        }

        override fun complete(): Completed {
            return Completed(originUrl, id, coverImageUrl, createTime, nowMilliseconds(), savePath)
        }

        override fun failed(error: Throwable?): Failed {
            return Failed(
                originUrl,
                id,
                coverImageUrl,
                m3u8Data,
                error,
                createTime,
                nowMilliseconds(),
                savePath
            )
        }
    }

    class Paused(
        originUrl: String,
        taskId: String,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        createTime: Long,
        updateTime: Long,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.Pause.ordinal,
        createTime,
        updateTime,
        savePath
    ) {
        override fun resume(): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds(), savePath
            )
        }
    }

    class Merging(
        originUrl: String,
        taskId: String,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        createTime: Long,
        updateTime: Long,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        DownloadTaskState.Downloading.ordinal,
        createTime,
        updateTime,
        savePath
    ) {

    }

    class Completed(
        originUrl: String,
        taskId: String,
        coverImageUrl: String?,
        createTime: Long,
        updateTime: Long,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.Completed.ordinal,
        createTime,
        updateTime,
        savePath
    )

    class Failed(
        originUrl: String,
        taskId: String,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val error: Throwable? = null,
        createTime: Long,
        updateTime: Long,
        savePath: String
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.Failed.ordinal,
        createTime,
        updateTime,
        savePath
    ) {
        override fun retry(): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds(), savePath
            )
        }
    }
}

interface TaskStateTransform<T : DownloadTask> {
    fun parse(m3u8Data: M3u8Data): M3u8DownloadTask.Parsed {
        throw NotImplementedError()
    }

    fun pending(): M3u8DownloadTask.Pending {
        throw NotImplementedError()
    }

    fun start(): M3u8DownloadTask.Downloading {
        throw NotImplementedError()
    }

    fun pause(): M3u8DownloadTask.Paused {
        throw NotImplementedError()
    }

    fun complete(): M3u8DownloadTask.Completed {
        throw NotImplementedError()
    }

    fun failed(error: Throwable? = null): M3u8DownloadTask.Failed {
        throw NotImplementedError()
    }

    fun resume(): M3u8DownloadTask.Downloading {
        throw NotImplementedError()
    }

    fun retry(): M3u8DownloadTask {
        throw NotImplementedError()
    }

    fun merge(): M3u8DownloadTask.Merging {
        throw NotImplementedError()
    }
}

fun M3u8DownloadTask.toM3u8TaskEntity(): M3u8TaskEntity {
    val state = when (this) {
        is M3u8DownloadTask.Completed -> DownloadTaskState.Completed
        is M3u8DownloadTask.Downloading -> DownloadTaskState.Downloading
        is M3u8DownloadTask.Failed -> DownloadTaskState.Failed
        is M3u8DownloadTask.Initially -> DownloadTaskState.Initially
        is M3u8DownloadTask.Parsed -> DownloadTaskState.Parsed
        is M3u8DownloadTask.Paused -> DownloadTaskState.Pause
        is M3u8DownloadTask.Pending -> DownloadTaskState.Pending
        is M3u8DownloadTask.Merging -> DownloadTaskState.Merging
    }
    return M3u8TaskEntity(
        originUrl,
        createTime,
        updateTime,
        coverImageUrl,
        state = state,
        taskId = id,
        savePath = savePath
    )
}

fun M3u8TaskEntity.toM3u8DownloadTask(m3u8Data: M3u8Data? = null): M3u8DownloadTask {
    return when (state) {
        DownloadTaskState.Downloading -> M3u8DownloadTask.Downloading(
            originUrl, taskId, coverImageUrl, m3u8Data!!, createTime, updateTime, savePath
        )

        DownloadTaskState.Completed -> M3u8DownloadTask.Completed(
            originUrl, taskId, coverImageUrl, createTime, updateTime, savePath
        )

        DownloadTaskState.Initially -> M3u8DownloadTask.Initially(
            originUrl, taskId, coverImageUrl, createTime, updateTime, savePath
        )

        DownloadTaskState.Failed -> M3u8DownloadTask.Failed(
            originUrl,
            taskId,
            coverImageUrl,
            m3u8Data!!,
            error = null,
            createTime,
            updateTime,
            savePath
        )

        DownloadTaskState.Pending -> M3u8DownloadTask.Pending(
            originUrl, taskId, m3u8Data!!, coverImageUrl, createTime, updateTime, savePath
        )

        DownloadTaskState.Pause -> M3u8DownloadTask.Paused(
            originUrl, taskId, coverImageUrl, m3u8Data!!, createTime, updateTime, savePath
        )

        DownloadTaskState.Parsed -> M3u8DownloadTask.Parsed(
            originUrl, taskId, m3u8Data!!, coverImageUrl, createTime, updateTime, savePath
        )

        DownloadTaskState.Merging -> M3u8DownloadTask.Merging(
            originUrl, taskId, coverImageUrl, m3u8Data!!, createTime, updateTime, savePath
        )
    }
}