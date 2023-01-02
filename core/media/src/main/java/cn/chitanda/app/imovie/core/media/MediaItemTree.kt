package cn.chitanda.app.imovie.core.media

import androidx.media3.common.MediaItem
import cn.chitanda.app.imovie.core.module.MovieDetail

/**
 *@author: Chen
 *@createTime: 2022/11/27 17:19
 *@description:
 **/
interface MediaItemTree {
    fun initialize(movieDetail: MovieDetail)
    fun getRootItem(): MediaItem?

    fun getItem(mediaId: String): MediaItem?

    fun getChildren(): List<MediaItem>

}