package cn.chitanda.app.imovie.core.media

import androidx.media3.session.MediaBrowser

/**
 *@author: Chen
 *@createTime: 2022/12/8 19:35
 *@description:
 **/
interface AppMediaController {
    val controller: MediaBrowser?
    fun viewModelInitialize(listener: Runnable?)
    fun release()
    fun removeViewModelListener()
}