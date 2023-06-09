package cn.chitanda.app.core.downloader.m3u8

import cn.chitanda.app.core.downloader.TestDownloadFileManager
import cn.chitanda.app.core.downloader.network.DownloadNetwork
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


/**
 * @author: Chen
 * @createTime: 2023/5/10 15:42
 * @description:
 **/
class M3u8ParserTest {

    private lateinit var parser: M3u8Parser

    @Before
    fun setup() {
        parser = M3u8Parser(DownloadNetwork(),TestDownloadFileManager())
    }

    @Test
    fun test_m3u8_parser() = runTest(dispatchTimeoutMs = 6000000L) {
//        val url = "https://v.gsuus.com/play/yb8OZ2oa/index.m3u8"
        val url = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8"
        val data = parser.parse(url)
        println(data.toString())
        assert(data != null)
    }
}