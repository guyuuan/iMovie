package cn.chitanda.app.core.downloader.repository

import cn.chitanda.app.core.downloader.db.DownloadTaskDatabase
import cn.chitanda.app.core.downloader.db.entites.ActualTaskEntity
import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.m3u8.MediaData
import cn.chitanda.app.core.downloader.task.ActualDownloadTask
import cn.chitanda.app.core.downloader.task.DownloadTaskState
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import cn.chitanda.app.core.downloader.task.toActualDownloadTask
import cn.chitanda.app.core.downloader.task.toActualTaskEntity
import cn.chitanda.app.core.downloader.task.toM3u8TaskEntity
import cn.chitanda.app.core.downloader.utils.nowMilliseconds
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * @author: Chen
 * @createTime: 2023/6/8 11:01
 * @description:
 **/

class DownloadTaskRepository(database: DownloadTaskDatabase) : IDownloadTaskRepository {
    private val actualTaskDao = database.actualTaskDao()
    private val m3u8TaskDao = database.m3u8TaskDao()
    private val lock = Mutex()
    override suspend fun createM3u8DownloadTask(
        originUrl: String, coverImage: String?
    ): M3u8DownloadTask = lock.withLock {
        val new = M3u8DownloadTask.Initially(originUrl, originUrl.md5(), coverImage)
        m3u8TaskDao.insertM3u8Task(new.toM3u8TaskEntity())
        return new
    }

    override suspend fun createActualDownloadTask(
        mediaData: MediaData, parentTaskId: String, downloadDir: String
    ): ActualDownloadTask = lock.withLock {
        val task = ActualTaskEntity(
            originUrl = mediaData.url,
            parentTaskId = parentTaskId,
            state = DownloadTaskState.initially,
            fileName = mediaData.url.substringAfterLast("/"),
            downloadDir = downloadDir,
            createTime = nowMilliseconds(),
        )
        val id = actualTaskDao.insertActualTask(task)
        return task.toActualDownloadTask(id.toString())
    }

    override suspend fun updateM3u8Task(task: M3u8DownloadTask) = lock.withLock {
        m3u8TaskDao.updateM3u8Task(task.toM3u8TaskEntity())
    }

    override suspend fun updateActualTask(task: ActualDownloadTask) = lock.withLock {
        actualTaskDao.updateActualTask(task.toActualTaskEntity())
    }

    override suspend fun getTaskIsFinished(id: String): Boolean = lock.withLock {
        actualTaskDao.getUndoneActualTasksCount(id) == 0
    }
}