package cn.chitanda.app.core.downloader

import androidx.annotation.VisibleForTesting
import cn.chitanda.app.core.downloader.executor.BlockExecutor
import cn.chitanda.app.core.downloader.executor.BlockWithTimeoutExecutor
import cn.chitanda.app.core.downloader.extension.plus
import cn.chitanda.app.core.downloader.file.AndroidDownloadFileManager
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
import cn.chitanda.app.core.downloader.usecase.UpdateTaskUseCase
import cn.chitanda.app.core.downloader.utils.nowMilliseconds
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    private val config: DownloadManagerConfig,
    private val fileManager: DownloadFileManager,
    coroutineScope: CoroutineScope,
    private val taskRepository: IDownloadTaskRepository
) : CoroutineScope by coroutineScope {
    constructor(
        fileManager: DownloadFileManager?,
        config: DownloadManagerConfig = DownloadManagerConfig(),
        coroutineContext: CoroutineContext,
        coroutineExceptionHandler: DownloadExceptionHandler = DownloadExceptionHandler(),
        taskRepository: IDownloadTaskRepository
    ) : this(
        config,
        fileManager ?: AndroidDownloadFileManager(config.cacheDirectory),
        CoroutineScope(coroutineContext + coroutineExceptionHandler),
        taskRepository = taskRepository
    ) {
        coroutineExceptionHandler.handler = ::exceptionHandler
    }

    class DownloadExceptionHandler : AbstractCoroutineContextElement(CoroutineExceptionHandler),
        CoroutineExceptionHandler {
        var handler: CoroutineExceptionHandlerFunction? = null

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            handler?.invoke(context, exception)
        }
    }

    private val network: IDownloadNetwork = DownloadNetwork()

    var downloadProgressListener: DownloadProgressListener? = null

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

                override fun omMerging(task: M3u8DownloadTask.Merging) {
                    TODO("Not yet implemented")
                }

                override fun onComplete(task: M3u8DownloadTask) {
                    if (task is M3u8DownloadTask.Completed) {
                        downloadProgressListener?.onComplete(task)
                    }
                }

                override fun onFailed(task: M3u8DownloadTask) {
                    if (task is M3u8DownloadTask.Failed) {
                        downloadProgressListener?.onFailed(task)
                    }
                }

                override fun onPause(task: M3u8DownloadTask) {
                    if (task is M3u8DownloadTask.Paused) {
                        downloadProgressListener?.onPause(task)
                    }
                }

                override fun onStart(task: M3u8DownloadTask) {
                    if (task is M3u8DownloadTask.Downloading) {
                        downloadProgressListener?.onStart(task)
                    }
                }

                override fun onPending(task: M3u8DownloadTask) {
                    if (task is M3u8DownloadTask.Pending) {
                        downloadProgressListener?.onPending(task)
                    }
                }

                override fun onDownloading(task: M3u8DownloadTask) {
                    launch { downloadExecutor.execute(task) }
                }

                override fun retryTask(task: M3u8DownloadTask) {
                    launch { downloadExecutor.execute(task) }
                }
            },
            useCase = UpdateTaskUseCase {
                launch {
                    taskRepository.updateM3u8Task(it)
                }
            })

    private val actualTaskQueueListener = object : ActualTaskQueueListener {
        override fun onComplete(task: ActualDownloadTask) {
            launch {
                val result = taskRepository.getTaskIsFinished(task.parentTaskId)
                if (result) {
                    taskQueue.updateById(task.parentTaskId) {
                        Timber.i("${this::class.simpleName} $id is finished")
                        complete()
                    }
                }
            }
        }

        override fun onFailed(task: ActualDownloadTask) {
            launch {
                if (taskRepository.getTaskIsFinished(task.id)) {
                    Timber.d("${task.originUrl} is finished by failed")
                    taskQueue.updateById(task.parentTaskId) {
                        failed(task.error)
                    }
                }
            }
        }

        override fun onDownloading(task: ActualDownloadTask) {
            taskExecutor.execute(task) {
                actualTaskQueue.updateById(task.id, check = false) {
                    copy(state = DownloadTaskState.Pending)
                }
                actualTaskQueue.getTaskById(task.id)
            }
        }

        override fun retryTask(task: ActualDownloadTask) {
            actualTaskQueue.updateById(task.id, check = false) {
                copy(state = DownloadTaskState.Pending)
            }
            Timber.d("${task.id}[${task.originUrl}] 进入重试 error = ${task.error}")
        }
    }

    //每个M3u8任务中具体的每个ts文件下载任务
    private val actualTaskQueue: ActualDownloadTaskQueue =
        ActualDownloadTaskQueue(autoRetry = config.autoRetry,
            maxRetryCount = config.retryCount,
            taskQueueListener = actualTaskQueueListener,
            useCase = UpdateTaskUseCase {
                launch { taskRepository.updateActualTask(it) }
            })
    private val parserExecutor = BlockExecutor(1, this, ::parserM3u8)
    private val downloadExecutor = BlockExecutor(config.maxTaskCount, this, ::download)

    private val taskExecutor =
        BlockWithTimeoutExecutor(config.actualTaskCount, this, ::downloadMediaData)

    private val m3u8Parser = M3u8Parser(network, fileManager)
    private suspend fun download(task: M3u8DownloadTask) {
        val data = when (task) {
            is M3u8DownloadTask.Parsed -> task.m3u8Data
            is M3u8DownloadTask.Pending -> task.m3u8Data
            is M3u8DownloadTask.Paused -> task.m3u8Data
            is M3u8DownloadTask.Failed -> task.m3u8Data
            else -> return
        }
        taskQueue.updateById(task.id) {
            start()
        }
        val downloadDir = task.savePath
        actualTaskQueue.addAll(data.mediaList.map { mediaData ->
            taskRepository.createActualDownloadTask(mediaData, task.id, downloadDir)
        })
        actualTaskQueue.start()
    }


    private suspend fun downloadMediaData(task: ActualDownloadTask) {
        try {
            actualTaskQueue.updateById(task.id) {
                Timber.d("$id [state = $state => ${DownloadTaskState.Downloading}]")
                copy(
                    state = DownloadTaskState.Downloading,
                    updateTime = nowMilliseconds(),
                    error = null
                )
            }
            val response = network.download(task.originUrl)
            response.body()?.use {
                val contentLength = it.contentLength()
                val file = fileManager.createFilePath(
                    fileName = task.fileName, task.downloadDir
                )
                if (fileManager.exists(file)) {
                    if (file.toFile().length() != contentLength) {
                        it.source().use { source ->
                            fileManager.writeToFile(
                                file, source
                            )
                        }
                    } else {
                        Timber.d("downloadMediaData: 文件已存在 ")
                    }
                }
            }
            actualTaskQueue.updateById(task.id) {
                Timber.d("${task.id}[$originUrl] 下载完成,路径为${fileManager.basePath + downloadDir + fileName}")
                copy(state = DownloadTaskState.Completed, updateTime = nowMilliseconds())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            actualTaskQueue.updateById(task.id) {
                copy(state = DownloadTaskState.Failed, updateTime = nowMilliseconds(), error = e)
            }
        }
    }


    fun startDownload(url: String, coverImageUrl: String? = null) {
        if (!taskQueue.containsByUrl(url)) {
            launch {
                taskQueue.offer(
                    taskRepository.createM3u8DownloadTask(url, coverImageUrl, fileManager.basePath)
                )
                taskQueue.start()
            }
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
        Timber.d(exception)
    }

    @VisibleForTesting
    suspend fun joinTestBlock() {
        coroutineContext[Job]?.join()
    }
}

class DownloadManagerConfig(
    val cacheDirectory: String? = null,
    val maxTaskCount: Int = 1,
    val actualTaskCount: Int = 8,
    val autoRetry: Boolean = true,
    val retryCount: Int = 3
)

interface DownloadProgressListener {
    fun onStart(task: M3u8DownloadTask.Downloading) {}
    fun onPause(task: M3u8DownloadTask.Paused) {}

    fun onPending(task: M3u8DownloadTask.Pending) {}

    fun onComplete(task: M3u8DownloadTask.Completed) {}

    fun onFailed(task: M3u8DownloadTask.Failed) {}


}
