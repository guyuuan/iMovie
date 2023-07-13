package cn.chitanda.lib.ffmpeg.knative

/**
 * @author: Chen
 * @createTime: 2023/7/5 13:50
 * @description:
 **/
class M3u8Merger {
    init {
        System.loadLibrary("knffmpeg")
    }

    external fun init()
}