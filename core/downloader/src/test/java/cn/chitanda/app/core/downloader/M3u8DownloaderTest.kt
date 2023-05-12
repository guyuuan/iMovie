package cn.chitanda.app.core.downloader

import cn.chitanda.app.core.downloader.file.DownloadFileManager
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okio.ForwardingFileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.Sink
import org.junit.Before
import org.junit.Test

/**
 * @author: Chen
 * @createTime: 2023/5/10 15:42
 * @description:
 **/
class M3u8DownloaderTest {
    private lateinit var downloader: M3u8Downloader

    @Before
    fun setup() {
        downloader = M3u8Downloader(OkHttpClient(), object : DownloadFileManager {
            private val fileSystem = object : ForwardingFileSystem(SYSTEM) {
                override fun sink(file: Path, mustCreate: Boolean): Sink {
                    file.parent?.let(::createDirectories)
                    return super.sink(file, mustCreate)
                }
            }

            override fun createFile(filename: String): Path {
//                FileSystem.SYSTEM.sink("/Users/chunjinchen/Download/1/$filename".toPath())
                val path = "/Users/chunjinchen/Download/1/$filename".toPath()
                fileSystem.sink(path).close()
                return path
            }
        })
    }

    @Test
    fun download_m3u8_test() = runTest(dispatchTimeoutMs = Long.MAX_VALUE){
        val url = "https://v.gsuus.com/play/yb8OZ2oa/index.m3u8"
        downloader.download(url)
    }
}