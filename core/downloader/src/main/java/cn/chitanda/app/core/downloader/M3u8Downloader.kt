package cn.chitanda.app.core.downloader

import cn.chitanda.app.core.downloader.executor.BlockExecutor
import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.file.DownloadFileManager
import cn.chitanda.app.core.downloader.m3u8.M3u8Parser
import cn.chitanda.app.core.downloader.m3u8.MediaData
import cn.chitanda.app.core.downloader.network.DownloadNetwork
import cn.chitanda.app.core.downloader.network.IDownloadNetwork
import cn.chitanda.app.core.downloader.queue.DownloadTaskQueue
import cn.chitanda.app.core.downloader.queue.M3u8DownloadTaskQueue
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.use
import timber.log.Timber
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * @author: Chen
 * @createTime: 2023/5/10 14:15
 * @description:
 **/

typealias CoroutineExceptionHandlerFunction = (CoroutineContext, Throwable) -> Unit

class M3u8Downloader(
    private val fileManager: DownloadFileManager,
    private val config: DownloadManagerConfig = DownloadManagerConfig(),
    coroutineScope: CoroutineScope,
) : CoroutineScope by coroutineScope {
    internal constructor(
        fileManager: DownloadFileManager,
        config: DownloadManagerConfig = DownloadManagerConfig(),
        coroutineContext: CoroutineContext,
        coroutineExceptionHandler: DownloadExceptionHandler = DownloadExceptionHandler()
    ) : this(fileManager, config, CoroutineScope(coroutineContext + coroutineExceptionHandler)) {
        coroutineExceptionHandler.handler = ::exceptionHandler
    }

    internal class DownloadExceptionHandler :
        AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
        var handler: CoroutineExceptionHandlerFunction? = null

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            handler?.invoke(context, exception)
        }
    }

    private val network: IDownloadNetwork = DownloadNetwork()
    private val taskQueue: DownloadTaskQueue<M3u8DownloadTask> = M3u8DownloadTaskQueue()
    private val parserExecutor = BlockExecutor(1, this, ::parserM3u8)
    private val downloadExecutor = BlockExecutor(config.maxTaskCount, this, ::download)

    private val taskExecutor = BlockExecutor(config.actualTaskCount, this, ::downloadMediaData)
    private val lock = Mutex()
    private val m3u8Parser = M3u8Parser(network)
    private fun download(task: M3u8DownloadTask) {
        val url = task.originUrl
        val data = when (task) {
            is M3u8DownloadTask.Parsed -> task.m3u8Data
            is M3u8DownloadTask.Pending -> task.m3u8Data
            is M3u8DownloadTask.Paused -> task.m3u8Data
            is M3u8DownloadTask.Failed -> task.m3u8Data
            else -> return
        }
        val downloadDir = url.md5()

        for (mediaData in data.mediaList) {
            launch {
                taskExecutor.execute(downloadDir to mediaData)
            }.invokeOnCompletion {
                println("${mediaData.index} send success")
            }
        }
    }

    private suspend fun downloadMediaData(pair: Pair<String, MediaData>) {
        val data = pair.second
        val downloadDir = pair.first
        val response = network.download(data.url)
        response.body()?.use {
            it.source().use { source ->
                fileManager.writeToFile(
                    fileManager.createFilePath(
                        fileName = data.url.substringAfterLast(
                            "/"
                        ), downloadDir
                    ), source
                )
            }
        }
        println("${data.index} 下载完成")
    }


    fun startDownload(url: String, id: Int, coverImageUrl: String? = null) {
        if (!taskQueue.containsByUrl(url)) {
            taskQueue.offer(
                M3u8DownloadTask.Initially(
                    originUrl = url, taskId = id, coverImageUrl = coverImageUrl
                )
            )
            launch {
                peekTaskQueue()
            }
        } else {
            Timber.w("$url has added to download task queue")
        }

    }

    private suspend fun peekTaskQueue() {
        lock.withLock {
            when (val task = taskQueue.peek()) {
                is M3u8DownloadTask.Completed -> TODO()
                is M3u8DownloadTask.Downloading -> TODO()
                is M3u8DownloadTask.Failed -> TODO()
                is M3u8DownloadTask.Initially -> {
                    parserExecutor.execute(task)
                }

                is M3u8DownloadTask.Parsed -> {
                    downloadExecutor.execute(task)
                }

                is M3u8DownloadTask.Paused -> TODO()
                is M3u8DownloadTask.Pending -> TODO()
            }
        }
    }


    private suspend fun parserM3u8(task: M3u8DownloadTask.Initially) {
        val m3u8 = m3u8Parser.parse(task.originUrl) ?: throw Exception()
        taskQueue.updateHead {
            parse(m3u8)
        }
        peekTaskQueue()
    }


    private fun exceptionHandler(context: CoroutineContext, exception: Throwable) {
        println(exception)
    }
}

class DownloadManagerConfig(
    val maxTaskCount: Int = 1,
    val actualTaskCount: Int = 8,
    val autoRetry: Boolean = false,
    val retryCount: Int = 3
)
