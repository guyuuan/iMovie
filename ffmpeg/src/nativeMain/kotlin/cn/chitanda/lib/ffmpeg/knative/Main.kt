@file:OptIn(ExperimentalForeignApi::class)

package cn.chitanda.lib.ffmpeg.knative

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import platform.android.ANDROID_LOG_INFO
import platform.android.JNIEnvVar
import platform.android.__android_log_print
import platform.android.jobject
/**
 * @author: Chen
 * @createTime: 2023/7/3 15:51
 * @description:
 **/
internal const val TAG = "ffmpeg_kotlin_native"

@CName("Java_cn_chitanda_lib_ffmpeg_knative_M3u8Merger_init")
fun startInit(env: CPointer<JNIEnvVar>, thiz: jobject) {
    memScoped {
        init()
        __android_log_print(ANDROID_LOG_INFO.toInt(), TAG, "start init")
    }
}

expect fun init()