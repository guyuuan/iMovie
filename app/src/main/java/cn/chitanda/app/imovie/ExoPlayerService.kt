package cn.chitanda.app.imovie

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import cn.chitanda.app.imovie.core.media.CoilBitmapLoader
import cn.chitanda.app.imovie.core.media.MediaItemTree
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2022/11/26 18:26
 *@description:
 **/
private const val TAG = "ExoPlayerService"

@AndroidEntryPoint
class ExoPlayerService : MediaLibraryService() {

    @Inject
    lateinit var mediaItemTree: MediaItemTree
    private lateinit var mediaSession: MediaLibrarySession
    private lateinit var player: ExoPlayer
    private val mediaSessionCallback = CustomMediaLibrarySessionCallback()

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        Log.d(TAG, "onGetSession: ")
        return mediaSession
    }

    @androidx.media3.common.util.UnstableApi
    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
    }

    override fun onDestroy() {
        player.release()
        mediaSession.release()
        super.onDestroy()
    }

    @androidx.media3.common.util.UnstableApi
    private fun initializeSessionAndPlayer() {
        player = ExoPlayer.Builder(this)
//            .setTrackSelector(DefaultTrackSelector(this).apply {
//                setParameters(buildUponParameters().setMaxVideoSizeSd())
//            })
//            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
//            .setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            .setAudioAttributes(AudioAttributes.DEFAULT, true).build()

        val sessionActivityPendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@ExoPlayerService, MainActivity::class.java))
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        mediaSession = MediaLibrarySession.Builder(this, player, mediaSessionCallback)
            .setBitmapLoader(CoilBitmapLoader(this.applicationContext))
            .setSessionActivity(sessionActivityPendingIntent).build()
//        mediaSession = MediaLibrarySession.Builder(this, player, mediaSessionCallback).build()
    }

    private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            Log.d(TAG, "onConnect: ")
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }


        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            super.onPostConnect(session, controller)
            Log.d(TAG, "onPostConnect: ")
        }

        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<Void>> {
            Log.d(TAG, "onSubscribe: $parentId")
            val children =
                mediaItemTree.getChildren()
//            mediaSession.player.setMediaItems(children)
            session.notifyChildrenChanged(browser, parentId, children.size, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Log.d(TAG, "onGetLibraryRoot: ")
            val item =
                mediaItemTree.getRootItem() ?: return Futures.immediateFuture(
                    LibraryResult.ofError(
                        LibraryResult.RESULT_ERROR_BAD_VALUE
                    )
                )
            return Futures.immediateFuture(LibraryResult.ofItem(item, null))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Log.d(TAG, "onGetItem: ")
            val item = mediaItemTree.getItem(mediaId) ?: return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            )
            return Futures.immediateFuture(LibraryResult.ofItem(item, null))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): ListenableFuture<List<MediaItem>> {
            val list = mediaItems.map {
                mediaItemTree.getItem(it.mediaId) ?: it
            }
            Log.d(TAG, "onAddMediaItems: ")
            return Futures.immediateFuture(list)
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val children = mediaItemTree.getChildren()
            Log.d(TAG, "onGetChildren: $children")
            session.notifyChildrenChanged(browser, parentId, children.size, null)
            return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
        }
    }


}