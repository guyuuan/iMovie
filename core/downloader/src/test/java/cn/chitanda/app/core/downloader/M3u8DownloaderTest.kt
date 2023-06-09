package cn.chitanda.app.core.downloader

import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.file.DownloadFileManager
import cn.chitanda.app.core.downloader.repository.TestTaskRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toPath
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import kotlin.test.assertTrue

/**
 * @author: Chen
 * @createTime: 2023/5/10 15:42
 * @description:
 **/
class M3u8DownloaderTest {
    private lateinit var downloader: M3u8Downloader
    private val coroutineContext =
        Dispatchers.IO + SupervisorJob() + CoroutineName("DownloadTestContext")

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())
        downloader = M3u8Downloader(
            fileManager = TestDownloadFileManager(),
            coroutineContext = coroutineContext,
            taskRepository = TestTaskRepository()
        )
    }

    @Test
    fun download_m3u8_test() = runBlocking {
        val url = "https://v.gsuus.com/play/yb8OZ2oa/index.m3u8"
        downloader.startDownload(url)
        downloader.joinTestBlock()
//        while (true){}
    }

    @Test
    fun string_md5_test() {
        assertTrue {
            val md5 = "https://v.gsuus.com/play/yb8OZ2oa/index.m3u8".md5()
            println("md5 = $md5")
            md5 == "fb1af886a60c651cff7e0b3680bf92f9"
        }
    }
}

class TestDownloadFileManager : DownloadFileManager(basePath = SYSTEM_TEMPORARY_DIRECTORY, SYSTEM) {

    override fun createFilePath(fileName: String, dir: String?): Path {
        return "$basePath${Path.DIRECTORY_SEPARATOR}${if (dir != null) "$dir${Path.DIRECTORY_SEPARATOR}" else ""}$fileName".toPath()
    }
}