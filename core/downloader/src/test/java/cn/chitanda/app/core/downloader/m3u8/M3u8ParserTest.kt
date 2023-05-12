package cn.chitanda.app.core.downloader.m3u8

import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
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
        parser = M3u8Parser(OkHttpClient())
    }

    @Test
    fun test_m3u8_parser() = runTest (dispatchTimeoutMs = 6000000L){
        val url = "https://v.gsuus.com/play/yb8OZ2oa/index.m3u8"
        val data = parser.parse(url)
        println(data.toString())
        assert(data != null)
    }
}