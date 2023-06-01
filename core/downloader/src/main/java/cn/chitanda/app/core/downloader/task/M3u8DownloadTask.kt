package cn.chitanda.app.core.downloader.task

import cn.chitanda.app.core.downloader.m3u8.M3u8Data

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
) : DownloadTask(), TaskStateTransform<ActualDownloadTask> {

    class Initially(
        originUrl: String, taskId: Int, coverImageUrl: String?,
    ) : M3u8DownloadTask(originUrl, taskId, coverImageUrl) {
        override fun parse(m3u8Data: M3u8Data): Parsed {
            return Parsed(originUrl, id, m3u8Data, coverImageUrl)
        }
    }

    class Parsed(originUrl: String, taskId: Int, val m3u8Data: M3u8Data, coverImageUrl: String?) :
        M3u8DownloadTask(
            originUrl, taskId, coverImageUrl, priority = DownloadTaskState.initially.ordinal
        ) {
        override fun start(tasks: List<ActualDownloadTask>): Downloading {
            return Downloading(originUrl, id, coverImageUrl, m3u8Data, tasks)
        }

        override fun pending(): Pending = Pending(originUrl, id, m3u8Data, coverImageUrl)
    }

    class Pending(originUrl: String, taskId: Int, val m3u8Data: M3u8Data, coverImageUrl: String?) :
        M3u8DownloadTask(
            originUrl, taskId, coverImageUrl, priority = DownloadTaskState.pending.ordinal
        ) {
        override fun start(tasks: List<ActualDownloadTask>): Downloading {
            return Downloading(originUrl, id, coverImageUrl, m3u8Data, tasks)
        }
    }

    class Downloading(
        originUrl: String,
        taskId: Int,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val tasks: List<ActualDownloadTask>
    ) : M3u8DownloadTask(originUrl, taskId, coverImageUrl) {
        override fun pause(): Paused {
            return Paused(originUrl, id, coverImageUrl, m3u8Data, tasks)
        }

        override fun complete(): Completed {
            return Completed(originUrl, id, coverImageUrl)
        }

        override fun failed(error: Throwable): Failed {
            return Failed(originUrl, id, coverImageUrl, m3u8Data, error)
        }
    }

    class Paused(
        originUrl: String,
        taskId: Int,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val tasks: List<ActualDownloadTask>
    ) : M3u8DownloadTask(
        originUrl, taskId, coverImageUrl,
        priority = DownloadTaskState.pause.ordinal
    ) {
        override fun resume(): Downloading {
            return Downloading(originUrl, id, coverImageUrl, m3u8Data, tasks)
        }
    }

    class Completed(
        originUrl: String, taskId: Int, coverImageUrl: String?
    ) : M3u8DownloadTask(originUrl, taskId, coverImageUrl)

    class Failed(
        originUrl: String,
        taskId: Int,
        coverImageUrl: String?,
        val m3u8Data: M3u8Data,
        val error: Throwable
    ) : M3u8DownloadTask(
        originUrl, taskId, coverImageUrl,
        priority = DownloadTaskState.failed.ordinal
    ) {
        override fun retry(tasks: List<ActualDownloadTask>): Downloading {
            return Downloading(originUrl, id, coverImageUrl, m3u8Data, tasks)
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

