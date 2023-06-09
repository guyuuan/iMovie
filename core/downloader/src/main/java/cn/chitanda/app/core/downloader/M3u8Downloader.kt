package cn.chitanda.app.core.downloader

import androidx.annotation.VisibleForTesting
import cn.chitanda.app.core.downloader.executor.BlockExecutor
import cn.chitanda.app.core.downloader.executor.BlockWithTimeoutExecutor
import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.extension.plus
import cn.chitanda.app.core.downloader.file.DownloadFileManager
import cn.chitanda.app.core.downloader.m3u8.M3u8Parser
import cn.chitanda.app.core.downloader.network.DownloadNetwork
import cn.chitanda.app.core.downloader.network.IDownloadNetwork
import cn.chitanda.app.core.downloader.queue.ActualDownloadTaskQueue
import cn.chitanda.app.core.downloader.queue.ActualTaskQueueListener
import cn.chitanda.app.core.downloader.queue.DownloadTaskQueue
import cn.chitanda.app.core.downloader.queue.M3u8DownloadTaskQueue
import cn.chitanda.app.core.downloader.queue.M3u8TaskQueueListener
import cn.chitanda.app.core.downloader.repository.IDownloadTaskRepository
import cn.chitanda.app.core.downloader.task.ActualDownloadTask
import cn.chitanda.app.core.downloader.task.DownloadTaskState
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import cn.chitanda.app.core.downloader.utils.nowMilliseconds
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
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

class M3u8Downloader private constructor(
    private val fileManager: DownloadFileManager,
    private val config: DownloadManagerConfig = DownloadManagerConfig(),
    coroutineScope: CoroutineScope,
    private val taskRepository: IDownloadTaskRepository
) : CoroutineScope by coroutineScope {
    internal constructor(
        fileManager: DownloadFileManager,
        config: DownloadManagerConfig = DownloadManagerConfig(),
        coroutineContext: CoroutineContext,
        coroutineExceptionHandler: DownloadExceptionHandler = DownloadExceptionHandler(),
        taskRepository: IDownloadTaskRepository
    ) : this(
        fileManager,
        config,
        CoroutineScope(coroutineContext + coroutineExceptionHandler),
        taskRepository = taskRepository
    ) {
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

    //M3u8下载任务队列
    private val taskQueue: DownloadTaskQueue<M3u8DownloadTask> =
        M3u8DownloadTaskQueue(autoRetry = config.autoRetry,
            maxRetryCount = config.maxTaskCount,
            taskQueueListener = object : M3u8TaskQueueListener {
                override fun onParse(task: M3u8DownloadTask.Initially) {
                    launch {
                        parserExecutor.execute(task)
                    }
                }

                override fun onComplete(task: M3u8DownloadTask) {

                }

                override fun onFailed(task: M3u8DownloadTask) {
                    
                }

                override fun executeTask(task: M3u8DownloadTask) {
                    launch { downloadExecutor.execute(task) }
                }

                override fun retryTask(task: M3u8DownloadTask) {
                    launch { downloadExecutor.execute(task) }
                }
            })

    private val actualTaskQueueListener = object : ActualTaskQueueListener {
        override fun onComplete(task: ActualDownloadTask) {

        }

        override fun onFailed(task: ActualDownloadTask) {

        }

        override fun executeTask(task: ActualDownloadTask) {
            launch {
                taskExecutor.execute(task) {
                    actualTaskQueue.updateById(task.id) {
                        copy(state = DownloadTaskState.pending)
                    }
//                    println("${task.id}[${task.originUrl}] 开始等待")
                    actualTaskQueue.getTaskById(task.id)
                }
            }
        }

        override fun retryTask(task: ActualDownloadTask) {
            launch {
                taskExecutor.execute(task)
                println("${task.id}[${task.originUrl}] 重新开始下载 error = ${task.error}")
            }
        }
    }

    //每个M3u8任务中具体的每个ts文件下载任务
    private val actualTaskQueue: ActualDownloadTaskQueue = ActualDownloadTaskQueue(
        autoRetry = config.autoRetry,
        maxRetryCount = config.retryCount,
        taskQueueListener = actualTaskQueueListener
    )
    private val parserExecutor = BlockExecutor(1, this, ::parserM3u8)
    private val downloadExecutor = BlockExecutor(config.maxTaskCount, this, ::download)

    private val taskExecutor =
        BlockWithTimeoutExecutor(config.actualTaskCount, this, ::downloadMediaData)
    private val lock = Mutex()
    private val m3u8Parser = M3u8Parser(network, fileManager)
    private suspend fun download(task: M3u8DownloadTask) {
        val url = task.originUrl
        val data = when (task) {
            is M3u8DownloadTask.Parsed -> task.m3u8Data
            is M3u8DownloadTask.Pending -> task.m3u8Data
            is M3u8DownloadTask.Paused -> task.m3u8Data
            is M3u8DownloadTask.Failed -> task.m3u8Data
            else -> return
        }
        val downloadDir = url.md5()
        actualTaskQueue.addAll(data.mediaList.map { mediaData ->
            taskRepository.createActualDownloadTask(mediaData, task.id, downloadDir)
        })
        actualTaskQueue.start()
    }


    private suspend fun downloadMediaData(task: ActualDownloadTask) {
        try {
            actualTaskQueue.updateById(task.id) {
                println("$id [state = $state => ${DownloadTaskState.downloading}]")
                copy(
                    state = DownloadTaskState.downloading,
                    updateTime = nowMilliseconds(),
                    error = null
                )
            }
            val response = network.download(task.originUrl)
            response.body()?.use {
                it.source().use { source ->
                    fileManager.writeToFile(
                        fileManager.createFilePath(
                            fileName = task.fileName, task.downloadDir
                        ), source
                    )
                }
            }
            actualTaskQueue.updateById(task.id) {
                println("${task.id}[$originUrl] 下载完成,路径为${fileManager.basePath + downloadDir + fileName}")
                copy(state = DownloadTaskState.completed, updateTime = nowMilliseconds())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            actualTaskQueue.updateById(task.id) {
                copy(state = DownloadTaskState.failed, updateTime = nowMilliseconds(), error = e)
            }
        }
    }


    suspend fun startDownload(url: String, coverImageUrl: String? = null) {
        if (!taskQueue.containsByUrl(url)) {
            taskQueue.offer(
                taskRepository.createM3u8DownloadTask(url)
            )
            taskQueue.start()
        } else {
            Timber.w("$url has added to download task queue")
        }

    }


    private suspend fun parserM3u8(task: M3u8DownloadTask.Initially) {
        val m3u8 = m3u8Parser.parse(task.originUrl) ?: throw Exception()
        taskQueue.updateHead {
            parse(m3u8)
        }
    }


    private fun exceptionHandler(context: CoroutineContext, exception: Throwable) {
        println(exception)
    }

    @VisibleForTesting
    suspend fun joinTestBlock() {
        coroutineContext[Job]?.join()
    }
}

class DownloadManagerConfig(
    val maxTaskCount: Int = 1,
    val actualTaskCount: Int = 2,
    val autoRetry: Boolean = true,
    val retryCount: Int = 3
)
