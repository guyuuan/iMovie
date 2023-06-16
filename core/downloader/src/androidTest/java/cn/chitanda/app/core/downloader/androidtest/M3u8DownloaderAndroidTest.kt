package cn.chitanda.app.core.downloader.androidtest

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.chitanda.app.core.downloader.DownloadProgressListener
import cn.chitanda.app.core.downloader.M3u8Downloader
import cn.chitanda.app.core.downloader.db.DownloadTaskDatabase
import cn.chitanda.app.core.downloader.file.DownloadFileManager
import cn.chitanda.app.core.downloader.repository.DownloadTaskRepository
import cn.chitanda.app.core.downloader.task.M3u8DownloadTask
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Path.Companion.toPath
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import kotlin.coroutines.resume

/**
 * @author: Chen
 * @createTime: 2023/5/10 15:42
 * @description:
 **/
@RunWith(AndroidJUnit4::class)
class M3u8DownloaderAndroidTest {
    private lateinit var downloader: M3u8Downloader
    private val coroutineContext =
        Dispatchers.IO + SupervisorJob() + CoroutineName("DownloadTestContext")
    private lateinit var database: DownloadTaskDatabase

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = DownloadTaskDatabase.createTestDatabase(context)
        downloader = M3u8Downloader(
            fileManager = TestDownloadFileManager(),
            coroutineContext = coroutineContext,
            taskRepository = DownloadTaskRepository(database)
        )
    }

    @Test
    fun download_m3u8_test() = runTest (dispatchTimeoutMs = Long.MAX_VALUE ){
//        val url = "https://v.gsuus.com/play/yb8OZ2oa/index.m3u8"
        val url =
            "http://1257120875.vod2.myqcloud.com/0ef121cdvodtransgzp1257120875/3055695e5285890780828799271/v.f230.m3u8"
        downloader.startDownload(url)
        val task = awaitDownload(url)
        assert(task is M3u8DownloadTask.Completed)
    }

    private suspend fun awaitDownload(url: String) =
        suspendCancellableCoroutine<M3u8DownloadTask> { coroutine ->
            downloader.downloadProgressListener = object : DownloadProgressListener {
                override fun onComplete(task: M3u8DownloadTask.Completed) {
                    if (task.originUrl == url) {
                        coroutine.resume(task)
                    }
                }

                override fun onFailed(task: M3u8DownloadTask.Failed) {
                    if (task.originUrl == url) {
                        coroutine.resume(task)
                    }
                }
            }
        }


    @After
    fun close() {
        database.close()
    }
}

class TestDownloadFileManager : DownloadFileManager(basePath = SYSTEM_TEMPORARY_DIRECTORY, SYSTEM) {

    override fun createFilePath(fileName: String, dir: String?): Path {
        return "$basePath${Path.DIRECTORY_SEPARATOR}${if (dir != null) "$dir${Path.DIRECTORY_SEPARATOR}" else ""}$fileName".toPath()
    }
}