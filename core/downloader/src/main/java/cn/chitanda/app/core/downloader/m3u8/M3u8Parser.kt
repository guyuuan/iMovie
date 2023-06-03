package cn.chitanda.app.core.downloader.m3u8

import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.KEYFORMAT_IDENTITY
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.METHOD_AES_128
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.METHOD_NONE
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_ATTR_BYTERANGE
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_IV
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_KEYFORMAT
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_MEDIA_DURATION
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_MEDIA_SEQUENCE
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_METHOD
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_TARGET_DURATION
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_URI
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.REGEX_VERSION
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_DISCONTINUITY
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_ENDLIST
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_INIT_SEGMENT
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_KEY
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_MEDIA_DURATION
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_MEDIA_SEQUENCE
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_STREAM_INF
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_TARGET_DURATION
import cn.chitanda.app.core.downloader.m3u8.M3U8Constants.TAG_VERSION
import cn.chitanda.app.core.downloader.network.IDownloadNetwork
import kotlinx.coroutines.withTimeout
import okio.EOFException
import java.net.URL

/**
 * @author: Chen
 * @createTime: 2023/5/10 14:48
 * @description:
 **/
internal class M3u8Parser(private val network: IDownloadNetwork) {
    suspend fun parse(url: String): M3u8Data? {
        val handler = ParserHandler(url)
        return try {
            val response = network.download(url)
            val data = response.body()?.use {
                it.source().use { source ->
                    withTimeout(2000L){
                        while (true) {
                            try {
                                handler.start(source.readUtf8LineStrict())
                            } catch (e: EOFException) {
                                break
                            }
                        }
                    }
                    handler.create()
                }
            }
            if (data?.hasStreamInfo == true && data.mediaList.firstOrNull() != null) {
                data.mediaList.firstOrNull()?.url?.let { parse(it) }
            } else {
                data
            }
        } catch (_: Throwable) {
            null
        }
    }

}

class ParserHandler(url: String) {
    //    private val parsers:List<Parser> = Parse
    private var builder = M3u8DataBuilder(url)

    private val parsers = listOf(
        Parser.MediaDurationParser,
        Parser.TargetDurationParser,
        Parser.VersionParser,
        Parser.MediaSequenceParser,
        Parser.StreamInfoParser,
        Parser.StreamInfoParser,
        Parser.DiscontinuityParser,
        Parser.KeyParser,
        Parser.InitSegmentParser,
        Parser.EndListParser,
        Parser.TsUrlParser
    )

    fun start(line: String) {
        if (line.isNotEmpty()) {
            for (parser in parsers) {
                val `continue` = parser.onParse(line, this)
                if (!`continue`) break
            }
        }
    }

    fun next(): Boolean = true

    fun resolve(resovler: M3u8DataBuilder.() -> Unit): Boolean {
        builder.resovler()
        return false
    }

    fun create() = builder.build()
}

class M3u8DataBuilder(val url: String) {
    private val tsList: MutableList<MediaData> = mutableListOf()
    var method: String? = null
    var tsDuration: Float = 0f
    var targetDuration: Float = 0f
    var version: Int = 0
    var sequence: Int = 0
    var hasStreamInfo = false
    var hasDiscontinuity = false
    var hasKey = false
    var encryptionIV: String? = null
    var hasInitSegment: Boolean = false
    var encryptionKeyUrl: String? = null
    var initSegmentUri: String? = null
    var segmentByteRange: String? = null
    var tsIndex: Int = 0
    var hasEndList: Boolean = false

    fun addTsData(ts: MediaData) {
        tsList.add(ts)
    }

    fun build(): M3u8Data {
        return M3u8Data(
            url = url,
            version = version,
            targetDuration = targetDuration,
            mediaList = tsList,
            initSequence = sequence,
            hasEndList = hasEndList,
            hasStreamInfo = hasStreamInfo
        )
    }


}

sealed interface Parser {
    companion object {
        fun parseStringAttr(line: String, regex: Regex): String? {
            return regex.find(line)?.groupValues?.takeIf { it.size == 2 }?.lastOrNull()
        }

        fun parseOptionalStringAttr(line: String, regex: Regex): String? {
            return regex.find(line)?.groupValues?.getOrNull(1)
        }

        fun getM3U8AbsoluteUrl(url: String, line: String): String {

            if (url.startsWith("file://") || url.startsWith("/")) {
                return url
            }
            val baseUriPath: String = getBaseUrl(url)
            var hostUrl: String = getHostUrl(url)
            if (line.startsWith("//")) {
                return getSchema(url) + ":" + line
            }
            if (line.startsWith("/")) {
                val pathStr: String = getPathStr(url)
                val longestCommonPrefixStr: String = getLongestCommonPrefixStr(pathStr, line)
                if (hostUrl.endsWith("/")) {
                    hostUrl = hostUrl.substring(0, hostUrl.length - 1)
                }
                return hostUrl + longestCommonPrefixStr + line.substring(longestCommonPrefixStr.length)
            }
            return if (line.startsWith("http")) {
                line
            } else "$baseUriPath/$line"
        }

        private fun getLongestCommonPrefixStr(pathStr: String, line: String): String {
            if (pathStr.isEmpty() || line.isEmpty()) {
                return ""
            }
            if (pathStr == line) {
                return pathStr
            }
            val arr1: CharArray = pathStr.toCharArray()
            val arr2: CharArray = line.toCharArray()
            var j = 0
            while (j < arr1.size && j < arr2.size) {
                if (arr1[j] != arr2[j]) {
                    break
                }
                j++
            }
            return pathStr.substring(0, j)
        }

        private fun getPathStr(url: String): String {
            if (url.isEmpty()) return ""
            val hostUrl: String = getHostUrl(url)
            return if (hostUrl.isEmpty()) {
                url
            } else url.substring(hostUrl.length - 1)

        }

        private fun getSchema(url: String): String {
            return url.substringBefore("://", missingDelimiterValue = "")
        }

        private fun getHostUrl(url: String): String {
            if (url.isEmpty()) return ""
            return try {
                val formatURL = URL(url)
                val host = formatURL.host ?: return url
                val hostIndex = url.indexOf(host)
                if (hostIndex != -1) {
                    val port = formatURL.port
                    if (port != -1) {
                        url.substring(0, hostIndex + host.length) + ":" + port + "/"
                    } else {
                        url.substring(0, hostIndex + host.length) + "/"
                    }
                } else {
                    url
                }
            } catch (e: Exception) {
                url
            }
        }

        private fun getBaseUrl(url: String): String {
            return url.substringBeforeLast("/")
        }
    }

    fun onParse(line: String, handler: ParserHandler): Boolean


    object MediaDurationParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_MEDIA_DURATION)) {
                handler.resolve {
                    parseStringAttr(line, REGEX_MEDIA_DURATION)?.toFloatOrNull()?.also {
                        tsDuration = it
                    }
                }
            } else {
                handler.next()
            }
        }

    }

    object TargetDurationParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_TARGET_DURATION)) {
                handler.resolve {
                    parseStringAttr(line, REGEX_TARGET_DURATION)?.toFloatOrNull()?.also {
                        targetDuration = it
                    }
                }
            } else {
                handler.next()
            }
        }
    }

    object VersionParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_VERSION)) {
                handler.resolve {
                    parseStringAttr(line, REGEX_VERSION)?.toIntOrNull()?.also {
                        version = it
                    }
                }
            } else {
                handler.next()
            }
        }

    }

    object MediaSequenceParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_MEDIA_SEQUENCE)) {
                handler.resolve {
                    parseStringAttr(line, REGEX_MEDIA_SEQUENCE)?.toIntOrNull()?.also {
                        sequence = it
                    }
                }
            } else {
                handler.next()
            }
        }
    }

    object StreamInfoParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_STREAM_INF)) {
                handler.resolve {
                    hasStreamInfo = true
                }
            } else {
                handler.next()
            }
        }
    }

    object DiscontinuityParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_DISCONTINUITY)) {
                handler.resolve {
                    hasDiscontinuity = true
                }
            } else {
                handler.next()
            }
        }
    }

    object KeyParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_KEY)) {
                handler.resolve {
                    hasKey = true
                    method = parseOptionalStringAttr(line, REGEX_METHOD)
                    val keyFormat = parseOptionalStringAttr(line, REGEX_KEYFORMAT)
                    if (METHOD_NONE != method) {
                        encryptionIV = parseOptionalStringAttr(line, REGEX_IV)
                        if (keyFormat == null || KEYFORMAT_IDENTITY == keyFormat) {
                            if (METHOD_AES_128 == method) {
                                parseStringAttr(line, REGEX_URI)?.let {
                                    this.encryptionKeyUrl = getM3U8AbsoluteUrl(url, it)
                                }
                            }
                        }
                    }
                }
            } else {
                handler.next()
            }
        }
    }

    object InitSegmentParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_INIT_SEGMENT)) {
                val tempSegmentUrl = parseStringAttr(line, REGEX_URI)
                if (tempSegmentUrl?.isNotEmpty() == true) {
                    handler.resolve {
                        hasInitSegment = true
                        initSegmentUri = getM3U8AbsoluteUrl(url, tempSegmentUrl)
                        segmentByteRange = parseOptionalStringAttr(line, REGEX_ATTR_BYTERANGE)
                    }
                } else {
                    handler.next()
                }

            } else {
                handler.next()
            }
        }

    }

    object TsUrlParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (!line.startsWith("#")) {
                handler.resolve {
                    addTsData(
                        MediaData(
                            url = getM3U8AbsoluteUrl(url, line),
                            duration = tsDuration,
                            index = tsIndex++,
                            hasDiscontinuity = hasDiscontinuity,
                            method = method,
                            keyUrl = encryptionKeyUrl,
                            keyIV = encryptionIV,
                            initSegmentUrl = initSegmentUri,
                            segmentByteRange = segmentByteRange
                        )
                    )
                    hasDiscontinuity = false
                    hasKey = false
                    hasInitSegment = false
                    method = null
                    encryptionKeyUrl = null
                    encryptionIV = null
                    initSegmentUri = null
                    segmentByteRange = null
                }
            } else {
                handler.next()
            }
        }

    }

    object EndListParser : Parser {
        override fun onParse(line: String, handler: ParserHandler): Boolean {
            return if (line.startsWith(TAG_ENDLIST)) {
                handler.resolve {
                    hasEndList = true
                }
            } else {
                handler.next()
            }
        }

    }
}