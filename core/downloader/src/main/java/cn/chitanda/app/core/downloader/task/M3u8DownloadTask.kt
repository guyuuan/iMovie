package cn.chitanda.app.core.downloader.task

import cn.chitanda.app.core.downloader.m3u8.M3u8Data
import kotlinx.datetime.Clock

/**
 * @author: Chen
 * @createTime: 2023/5/31 14:33
 * @description:
 **/
sealed class M3u8DownloadTask(
    override val originUrl: String,
    override val id: Int,
    val coverImageUrl: String?,
    override val priority: Int = 0,
    override val createTime: Long,
    override val updateTime: Long,
) : DownloadTask(), TaskStateTransform<ActualDownloadTask> {
    companion object {
        private fun nowMilliseconds() = Clock.System.now().toEpochMilliseconds()
    }

    class Initially(
        originUrl: String, taskId: Int, coverImageUrl: String?,
        createTime: Long = nowMilliseconds(),
    ) : M3u8DownloadTask(
        originUrl, taskId, coverImageUrl, createTime = createTime, updateTime = createTime
    ) {
        override fun parse(m3u8Data: M3u8Data): Parsed {
            return Parsed(originUrl, id, m3u8Data, coverImageUrl, createTime, updateTime)
        }
    }


    class Parsed(
        originUrl: String,
        taskId: Int,
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
        override fun start(tasks: List<ActualDownloadTask>): Downloading {
            return Downloading(
                originUrl,
                id,
                coverImageUrl,
                m3u8Data,
                tasks,
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
        taskId: Int,
        val m3u8Data: M3u8Data,
        coverImageUrl: String?,
        createTime: Long,
        updateTime: Long
    ) : M3u8DownloadTask(
        originUrl, taskId, coverImageUrl, DownloadTaskState.pending.ordinal, createTime, updateTime
    ) {
        override fun start(tasks: List<ActualDownloadTask>): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, tasks, createTime, nowMilliseconds()
            )
        }
    }

    class Downloading(
        originUrl: String,
        taskId: Int,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val tasks: List<ActualDownloadTask>,
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
                originUrl, id, coverImageUrl, m3u8Data, tasks, createTime, nowMilliseconds()
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
        taskId: Int,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val tasks: List<ActualDownloadTask>,
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
                originUrl, id, coverImageUrl, m3u8Data, tasks, createTime, nowMilliseconds()
            )
        }
    }

    class Completed(
        originUrl: String, taskId: Int, coverImageUrl: String?, createTime: Long, updateTime: Long
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
        taskId: Int,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val error: Throwable,
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
        override fun retry(tasks: List<ActualDownloadTask>): Downloading {
            return Downloading(
                originUrl, id, coverImageUrl, m3u8Data, tasks, createTime, nowMilliseconds()
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

    fun start(tasks: List<ActualDownloadTask>): M3u8DownloadTask.Downloading {
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

    fun retry(tasks: List<T>): M3u8DownloadTask.Downloading {
        throw NotImplementedError()
    }
}

