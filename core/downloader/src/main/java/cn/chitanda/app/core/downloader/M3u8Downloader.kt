package cn.chitanda.app.core.downloader

import cn.chitanda.app.core.downloader.file.DownloadFileManager
import cn.chitanda.app.core.downloader.file.M3u8FileWriter
import cn.chitanda.app.core.downloader.m3u8.M3u8Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

/**
 * @author: Chen
 * @createTime: 2023/5/10 14:15
 * @description:
 **/
class M3u8Downloader(
    private val httpClient: OkHttpClient,
    private val fileManager: DownloadFileManager
) {
    suspend fun download(url: String) = withContext(Dispatchers.IO) {
        val data = M3u8Parser(httpClient).parse(url) ?: return@withContext
        data.ts.map { ts ->
            async {
                try {
                    val response =
                        httpClient.newCall(okhttp3.Request.Builder().url(ts).get().build())
                            .execute()
                    val body = response.body ?: return@async
                    val filename = response.headers["content-disposition"]?.split(";")
                        ?.find { s ->
                            s.startsWith("filename=")
                        }?.replace("filename=", "")
                        ?: ts.split("/").last()
                    val contentLength = body.contentLength()
                    val file = fileManager.createFile(filename)
                    M3u8FileWriter(file, body.byteStream(), data.key, data.method).use { w ->
                        w.write {}
                        println("${data.ts.indexOf(ts)} success >>> $filename")
                    }
                } catch (e: Throwable) {
                    println("${data.ts.indexOf(ts)} failed >>> $e")
                }
            }
        }.awaitAll()
    }
}

