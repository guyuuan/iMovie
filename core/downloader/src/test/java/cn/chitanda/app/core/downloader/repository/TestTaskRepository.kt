package cn.chitanda.app.core.downloader.repository

import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.m3u8.MediaData
import cn.chitanda.app.core.downloader.task.ActualDownloadTask
import cn.chitanda.app.core.downloader.task.DownloadTaskState
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import cn.chitanda.app.core.downloader.utils.nowMilliseconds

/**
 * @author: Chen
 * @createTime: 2023/6/8 15:01
 * @description:
 **/
class TestTaskRepository:IDownloadTaskRepository {
    private val m3u8 = mutableListOf<M3u8DownloadTask>()
    private val actual = mutableListOf<ActualDownloadTask>()
    override suspend fun createM3u8DownloadTask(
        originUrl: String,
        coverImage: String?
    ): M3u8DownloadTask {
        val new =  M3u8DownloadTask.Initially(originUrl, originUrl.md5(), coverImage)
        m3u8.add(new)
        return  new
    }

    override suspend fun createActualDownloadTask(
        mediaData: MediaData,
        parentTaskId: String,
        downloadDir: String
    ): ActualDownloadTask {
        val new = ActualDownloadTask(
            id = actual.size.toString(),
            originUrl = mediaData.url,
            parentTaskId = parentTaskId,
            state = DownloadTaskState.initially,
            fileName = mediaData.url.substringAfterLast("/"),
            downloadDir = downloadDir,
            createTime = nowMilliseconds(),
        )
        actual.add(new)
        return  new
    }
}