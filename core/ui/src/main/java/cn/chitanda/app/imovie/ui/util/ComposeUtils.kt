package cn.chitanda.app.imovie.ui.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * @author: Chen
 * @createTime: 2023/4/6 16:50
 * @description:
 **/
@Composable
fun findActivity(): Activity? {
    return LocalContext.current.findActivity()
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}