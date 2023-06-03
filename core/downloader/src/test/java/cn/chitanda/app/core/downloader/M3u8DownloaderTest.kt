package cn.chitanda.app.core.downloader

import cn.chitanda.app.core.downloader.extension.md5
import cn.chitanda.app.core.downloader.file.DownloadFileManager
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toPath
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertTrue

/**
 * @author: Chen
 * @createTime: 2023/5/10 15:42
 * @description:
 **/
class M3u8DownloaderTest {
    private lateinit var downloader: M3u8Downloader
    private val context = EmptyCoroutineContext + SupervisorJob()

    @Before
    fun setup() {
        downloader = M3u8Downloader(
            fileManager = TestDownloadFileManager(),
            coroutineContext = context
        )
    }

    @Test
    fun download_m3u8_test() = runBlocking(context) {
        val url = "https://v.gsuus.com/play/yb8OZ2oa/index.m3u8"
        downloader.startDownload(url, 1)
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

class TestDownloadFileManager : DownloadFileManager(SYSTEM) {

    override fun createFilePath(fileName: String, dir: String?): Path {
        return "/Users/chunjinchen/Downloads/${if (dir != null) "$dir/" else ""}$fileName".toPath()
    }
}