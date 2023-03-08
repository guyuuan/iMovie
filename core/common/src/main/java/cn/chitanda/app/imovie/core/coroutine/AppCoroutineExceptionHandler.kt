package cn.chitanda.app.imovie.core.coroutine

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * @author: Chen
 * @createTime: 2023/3/8 11:48
 * @description:
 **/
private const val TAG = "AppCoroutineExceptionHandler"
val AppCoroutineExceptionHandler = CoroutineExceptionHandler { cxt, throwable ->
    Log.e(TAG, "catch exception on $cxt: ", throwable)
}