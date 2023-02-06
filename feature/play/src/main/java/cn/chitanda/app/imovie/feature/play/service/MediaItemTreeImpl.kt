package cn.chitanda.app.imovie.feature.play.service

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_NONE
import androidx.media3.common.MimeTypes
import cn.chitanda.app.imovie.core.media.MediaItemTree
import cn.chitanda.app.imovie.core.model.MovieDetail
import cn.chitanda.app.imovie.core.model.PlaysSet
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2022/11/27 18:27
 *@description:
 **/

class MediaItemTreeImpl @Inject constructor() : MediaItemTree {
    private val medias = mutableListOf<MediaItem>()

    override fun initialize(movieDetail: MovieDetail) {
        medias.clear()
        medias.addAll(movieDetail.playSets.map {
            buildMediaItem(it, movieDetail)
        })
    }

    private fun buildMediaItem(set: PlaysSet, movie: MovieDetail): MediaItem {
        val metaData =
            MediaMetadata.Builder()
                .setTitle("${movie.name}-${set.name}")
//                .setSubtitle(set.name)
                .setFolderType(FOLDER_TYPE_NONE)
                .setGenre("Video")
                .setArtworkUri(Uri.parse(movie.pic))
                .setIsPlayable(true)
                .setAlbumTitle(movie.name)
                .build()
        return MediaItem.Builder()
            .setMediaMetadata(metaData)
            .setMediaId(set.mediaId)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
//            .setUri(Uri.parse("https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4")).build()
            .setUri(Uri.parse(set.url)).build()
    }

    override fun getRootItem(): MediaItem? {
        return medias.firstOrNull()
    }

    override fun getItem(mediaId: String): MediaItem? {
        return medias.find { it.mediaId == mediaId }
    }

    override fun getChildren(): List<MediaItem> {
        return medias
    }
}