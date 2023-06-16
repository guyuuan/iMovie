package cn.chitanda.app.core.downloader.repository

import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.m3u8.MediaData
import cn.chitanda.app.core.downloader.task.ActualDownloadTask
import cn.chitanda.app.core.downloader.task.DownloadTaskState
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import cn.chitanda.app.core.downloader.utils.nowMilliseconds
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * @author: Chen
 * @createTime: 2023/6/8 15:01
 * @description:
 **/
class TestTaskRepository : IDownloadTaskRepository {
    private val m3u8 = mutableMapOf<String, M3u8DownloadTask>()
    private val actual = mutableMapOf<String, ActualDownloadTask>()
    private val lock = Mutex()
    override suspend fun createM3u8DownloadTask(
        originUrl: String, coverImage: String?
    ): M3u8DownloadTask {
        return lock.withLock {
            val new = M3u8DownloadTask.Initially(originUrl, originUrl.md5(), coverImage)
            m3u8[new.id] = new
            new
        }
    }

    override suspend fun createActualDownloadTask(
        mediaData: MediaData, parentTaskId: String, downloadDir: String
    ): ActualDownloadTask {
        return lock.withLock {
            val new = ActualDownloadTask(
                id = actual.size.toString(),
                originUrl = mediaData.url,
                parentTaskId = parentTaskId,
                state = DownloadTaskState.initially,
                fileName = mediaData.url.substringAfterLast("/"),
                downloadDir = downloadDir,
                createTime = nowMilliseconds(),
            )
            actual[new.id] = new
            new
        }
    }

    override suspend fun updateM3u8Task(task: M3u8DownloadTask) {
        lock.withLock { m3u8[task.id] = task }
    }

    override suspend fun updateActualTask(task: ActualDownloadTask) {
        lock.withLock { actual[task.id] = task }
    }

    override suspend fun getTaskIsFinished(id: String): Boolean {
        return lock.withLock {
            actual.values.filter { it.parentTaskId == id }
                .none { (it.state != DownloadTaskState.completed && it.state != DownloadTaskState.failed) }
        }
    }
}