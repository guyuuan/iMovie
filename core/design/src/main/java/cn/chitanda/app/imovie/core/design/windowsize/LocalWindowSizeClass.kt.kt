package cn.chitanda.app.imovie.core.design.windowsize

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.compositionLocalOf

/**
 *@author: Chen
 *@createTime: 2022/12/8 16:58
 *@description:
 **/

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("LocalWindowSizeClass not initialized")
}