package cn.chitanda.app.core.downloader.m3u8

/**
 * @author: Chen
 * @createTime: 2023/5/10 15:00
 * @description:
 **/
data class M3u8Data(val url: String, val ts: List<String>, val key: ByteArray?, val method: String?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as M3u8Data

        if (url != other.url) return false
        if (ts != other.ts) return false
        if (key != null) {
            if (other.key == null) return false
            if (!key.contentEquals(other.key)) return false
        } else if (other.key != null) return false
        return method == other.method
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + ts.hashCode()
        result = 31 * result + (key?.contentHashCode() ?: 0)
        result = 31 * result + (method?.hashCode() ?: 0)
        return result
    }
}