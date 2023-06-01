package cn.chitanda.app.core.downloader.m3u8

/**
 * @author: Chen
 * @createTime: 2023/5/10 15:00
 * @description:
 **/
data class M3u8Data(
    val url: String,
    val mediaList: List<MediaData>,
    val version: Int,
    val targetDuration: Float,
    val initSequence: Int,
    val hasEndList: Boolean,
    val hasStreamInfo:Boolean
)

data class MediaData(
    val url: String,
    val index: Int,
    val duration: Float,
    val hasDiscontinuity: Boolean,
    val method: String?,
    val keyUrl: String?,
    val keyIV: String?,
    val initSegmentUrl: String?,
    val segmentByteRange: String?
) {
    val hasKey: Boolean = method != null && keyIV != null && keyUrl != null
    val hasInitSegment: Boolean = initSegmentUrl != null && segmentByteRange != null
}