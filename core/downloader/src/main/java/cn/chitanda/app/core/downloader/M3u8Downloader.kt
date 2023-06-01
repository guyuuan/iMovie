package cn.chitanda.app.core.downloader

import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.file.DownloadFileManager
import cn.chitanda.app.core.downloader.m3u8.M3u8Parser
import cn.chitanda.app.core.downloader.network.DownloadNetwork
import cn.chitanda.app.core.downloader.network.IDownloadNetwork
import cn.chitanda.app.core.downloader.queue.DownloadTaskQueue
import cn.chitanda.app.core.downloader.queue.M3u8DownloadTaskQueue
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.use
import timber.log.Timber

/**
 * @author: Chen
 * @createTime: 2023/5/10 14:15
 * @description:
 **/
class M3u8Downloader(
    private val fileManager: DownloadFileManager
) {
    private val network: IDownloadNetwork = DownloadNetwork()
    private val taskQueue: DownloadTaskQueue<M3u8DownloadTask> = M3u8DownloadTaskQueue()
    suspend fun download(url: String) = withContext(Dispatchers.IO) {
        val data = M3u8Parser(network).parse(url) ?: return@withContext
        val downloadDir = url.md5()
        for (mediaData in data.mediaList) {
            val response = network.download(mediaData.url)
            response.body()?.use {
                it.source().use { source ->
                    fileManager.writeToFile(
                        fileManager.createFilePath(
                            fileName = mediaData.url.substringAfterLast(
                                "/"
                            ), downloadDir
                        ), source
                    )
                }
            }
            println("${mediaData.index} 下载完成")
        }

    }

    fun startDownload(url: String, id: Int, coverImageUrl: String? = null) {
        if (!taskQueue.containsByUrl(url)) {
            taskQueue.offer(
                M3u8DownloadTask.Initially(
                    originUrl = url, taskId = id, coverImageUrl = coverImageUrl
                )
            )
        } else {
            Timber.w("$url has added to download task queue")
        }
    }
}

