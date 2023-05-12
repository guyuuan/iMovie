package cn.chitanda.app.core.downloader.m3u8

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author: Chen
 * @createTime: 2023/5/10 14:48
 * @description:
 **/
class M3u8Parser(private val httpClient: OkHttpClient) {
    suspend fun parse(url: String): M3u8Data? = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.newCall(Request.Builder().url(url).get().build()).execute()
            val m3u8 = response.body?.string() ?: return@withContext null
            readM3u8(url, m3u8)
        } catch (_: Throwable) {
            null
        }
    }

    private suspend fun readM3u8(url: String, string: String): M3u8Data? {
        val m3u8 = string.split("\n")
        if (m3u8.firstOrNull() != "#EXTM3U") return null
        var key: String? = null
        var method: String? = null
        val ts = mutableListOf<String>()
        for (s in m3u8) {
            if (s.startsWith("#EXT-X-KEY")) {
                key = s.substringAfter("URI=\"").substringBefore("\"")
                method = s.substringAfter("METHOD=").substringBefore(",")
                continue
            }
            if (s.startsWith("http") && s.endsWith(".ts")) {
                ts.add(s)
                continue
            }
            if (s.startsWith("http") && s.endsWith(".m3u8")) {
                parse(s)?.let { ts.addAll(it.ts) } ?: return null
                continue
            }
        }
        return M3u8Data(
            url = url, ts = ts, key = key?.let {
                httpClient.newCall(
                    Request.Builder().get().url(url.replaceAfterLast("/", it)).build()
                ).execute().body?.bytes()
            }, method = method
        )
    }
}