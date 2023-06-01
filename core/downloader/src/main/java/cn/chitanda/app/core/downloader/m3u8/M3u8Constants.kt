package cn.chitanda.app.core.downloader.m3u8

/**
 * @author: Chen
 * @createTime: 2023/5/30 16:11
 * @description:
 **/


object M3U8Constants {
    // base hls tag:
    const val PLAYLIST_HEADER = "#EXTM3U" // must
    const val TAG_PREFIX = "#EXT" // must
    const val TAG_VERSION = "#EXT-X-VERSION" // must
    const val TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE" // must
    const val TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION" // must
    const val TAG_MEDIA_DURATION = "#EXTINF" // must
    const val TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY" // Optional
    const val TAG_ENDLIST =
        "#EXT-X-ENDLIST" // It is not live if hls has '#EXT-X-ENDLIST' tag; Or it is.
    const val TAG_KEY = "#EXT-X-KEY" // Optional
    const val TAG_INIT_SEGMENT = "#EXT-X-MAP"

    // extra hls tag:
    // #EXT-X-PLAYLIST-TYPE:VOD       is not live
    // #EXT-X-PLAYLIST-TYPE:EVENT   is live, we also can try '#EXT-X-ENDLIST'
    const val TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE"
    const val TAG_STREAM_INF =
        "#EXT-X-STREAM-INF" // Multiple m3u8 stream, we usually fetch the first.
    const val TAG_ALLOW_CACHE = "EXT-X-ALLOW-CACHE" // YES : not live; NO: live
    val REGEX_TARGET_DURATION = "$TAG_TARGET_DURATION:(\\d+)\\b".toRegex()
    val REGEX_MEDIA_DURATION = "$TAG_MEDIA_DURATION:([\\d\\.]+)\\b".toRegex()
    val REGEX_VERSION = "$TAG_VERSION:(\\d+)\\b".toRegex()
    val REGEX_MEDIA_SEQUENCE = "$TAG_MEDIA_SEQUENCE:(\\d+)\\b".toRegex()
    const val METHOD_NONE = "NONE"
    const val METHOD_AES_128 = "AES-128"
    const val METHOD_SAMPLE_AES = "SAMPLE-AES"

    // Replaced by METHOD_SAMPLE_AES_CTR. Keep for backward compatibility.
    const val METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC"
    const val METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR"
    val REGEX_METHOD =
        ("METHOD=(" + METHOD_NONE + "|" + METHOD_AES_128 + "|" +
                METHOD_SAMPLE_AES + "|" + METHOD_SAMPLE_AES_CENC + "|" +
                METHOD_SAMPLE_AES_CTR + ")" + "\\s*(,|$)").toRegex()

    val REGEX_KEYFORMAT = "KEYFORMAT=\"(.+?)\"".toRegex()
    val REGEX_URI = "URI=\"(.+?)\"".toRegex()
    val REGEX_IV = "IV=([^,.*]+)".toRegex()
    const val KEYFORMAT_IDENTITY = "identity"
    val REGEX_ATTR_BYTERANGE = "BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"".toRegex()
}



