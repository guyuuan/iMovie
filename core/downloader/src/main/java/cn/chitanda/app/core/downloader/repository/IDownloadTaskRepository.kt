package cn.chitanda.app.core.downloader.repository

import cn.chitanda.app.core.downloader.m3u8.MediaData
import cn.chitanda.app.core.downloader.task.ActualDownloadTask
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask

interface IDownloadTaskRepository {
    suspend fun createM3u8DownloadTask(
        originUrl: String, coverImage: String? = null
    ): M3u8DownloadTask

    suspend fun createActualDownloadTask(
        mediaData: MediaData, parentTaskId: String, downloadDir: String
    ): ActualDownloadTask
}