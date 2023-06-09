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
) : DownloadTask(), TaskStateTransform<ActualDownloadTask> {

    class Initially(
        originUrl: String, taskId :String, coverImageUrl: String?,
        createTime: Long = nowMilliseconds(),
        updateTime: Long = createTime,
    ) : M3u8DownloadTask(
        originUrl, taskId, coverImageUrl, createTime = createTime, updateTime = updateTime
    ) {
        override fun parse(m3u8Data: M3u8Data): Parsed {
            return Parsed(originUrl, id, m3u8Data, coverImageUrl, createTime, updateTime)
        }
    }


    class Parsed(
        originUrl: String,
        taskId :String,
        val m3u8Data: M3u8Data,
        coverImageUrl: String?,
        createTime: Long,
        updateTime: Long
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.initially.ordinal,
        createTime,
        updateTime
    ) {
        override fun start(): Downloading {
            return Downloading(
                originUrl,
                id,
                coverImageUrl,
                m3u8Data,
                createTime = createTime,
                updateTime = nowMilliseconds()
            )
        }

        override fun pending(): Pending = Pending(
            originUrl,
            id,
            m3u8Data,
            coverImageUrl,
            createTime = createTime,
            updateTime = nowMilliseconds()
        )
    }

    class Pending(
        originUrl: String,
        taskId :String,
        val m3u8Data: M3u8Data,
        coverImageUrl: String?,
        createTime: Long,
        updateTime: Long
    ) : M3u8DownloadTask(
        originUrl, taskId, coverImageUrl, DownloadTaskState.pending.ordinal, createTime, updateTime
    ) {
        override fun start(): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds()
            )
        }
    }

    class Downloading(
        originUrl: String,
        taskId :String,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        createTime: Long,
        updateTime: Long
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        DownloadTaskState.downloading.ordinal,
        createTime,
        updateTime
    ) {
        override fun pause(): Paused {
            return Paused(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds()
            )
        }

        override fun complete(): Completed {
            return Completed(originUrl, id, coverImageUrl, createTime, nowMilliseconds())
        }

        override fun failed(error: Throwable): Failed {
            return Failed(
                originUrl, id, coverImageUrl, m3u8Data, error, createTime, nowMilliseconds()
            )
        }
    }

    class Paused(
        originUrl: String,
        taskId :String,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        createTime: Long,
        updateTime: Long
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.pause.ordinal,
        createTime,
        updateTime
    ) {
        override fun resume(): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds()
            )
        }
    }

    class Completed(
        originUrl: String, taskId :String, coverImageUrl: String?, createTime: Long, updateTime: Long
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.completed.ordinal,
        createTime,
        updateTime
    )

    class Failed(
        originUrl: String,
        taskId :String,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val error: Throwable? = null,
        createTime: Long,
        updateTime: Long
    ) : M3u8DownloadTask(
        originUrl,
        taskId,
        coverImageUrl,
        priority = DownloadTaskState.failed.ordinal,
        createTime,
        updateTime
    ) {
        override fun retry(): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, createTime, nowMilliseconds()
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

    fun failed(error: Throwable): M3u8DownloadTask.Failed {
        throw NotImplementedError()
    }

    fun resume(): M3u8DownloadTask.Downloading {
        throw NotImplementedError()
    }

    fun retry(): M3u8DownloadTask.Downloading {
        throw NotImplementedError()
    }
}

fun M3u8DownloadTask.toM3u8TaskEntity(): M3u8TaskEntity {
    val state = when (this) {
        is M3u8DownloadTask.Completed -> DownloadTaskState.completed
        is M3u8DownloadTask.Downloading -> DownloadTaskState.downloading
        is M3u8DownloadTask.Failed -> DownloadTaskState.failed
        is M3u8DownloadTask.Initially -> DownloadTaskState.initially
        is M3u8DownloadTask.Parsed -> DownloadTaskState.parsed
        is M3u8DownloadTask.Paused -> DownloadTaskState.pause
        is M3u8DownloadTask.Pending -> DownloadTaskState.pending
    }
    return M3u8TaskEntity(
        originUrl, createTime, updateTime, coverImageUrl, state = state, taskId = id
    )
}

fun M3u8TaskEntity.toM3u8DownloadTask(m3u8Data: M3u8Data? = null): M3u8DownloadTask {
    return when (state) {
        DownloadTaskState.downloading -> M3u8DownloadTask.Downloading(
            originUrl, taskId, coverImageUrl, m3u8Data!!, createTime, updateTime
        )

        DownloadTaskState.completed -> M3u8DownloadTask.Completed(
            originUrl, taskId, coverImageUrl, createTime, updateTime
        )

        DownloadTaskState.initially -> M3u8DownloadTask.Initially(
            originUrl, taskId, coverImageUrl, createTime, updateTime
        )

        DownloadTaskState.failed -> M3u8DownloadTask.Failed(
            originUrl, taskId, coverImageUrl, m3u8Data!!, error = null, createTime, updateTime
        )

        DownloadTaskState.pending -> M3u8DownloadTask.Pending(
            originUrl, taskId, m3u8Data!!, coverImageUrl, createTime, updateTime
        )

        DownloadTaskState.pause -> M3u8DownloadTask.Paused(
            originUrl, taskId, coverImageUrl, m3u8Data!!, createTime, updateTime
        )

        DownloadTaskState.parsed -> M3u8DownloadTask.Parsed(
            originUrl, taskId, m3u8Data!!, coverImageUrl, createTime, updateTime
        )
    }
}