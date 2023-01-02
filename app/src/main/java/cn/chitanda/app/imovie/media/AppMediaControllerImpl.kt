package cn.chitanda.app.imovie.media

import android.app.Activity
import android.content.ComponentName
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import cn.chitanda.app.imovie.ExoPlayerService
import cn.chitanda.app.imovie.core.media.AppMediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.lang.ref.WeakReference

class AppMediaControllerImpl : AppMediaController {
    private var controllerFuture: ListenableFuture<MediaBrowser>? = null
    private var weak: WeakReference<Activity>? = null
    private val activity: Activity?
        get() = weak?.get()
    private var futureListener: Runnable? = null

    fun setActivity(activity: Activity) {
        weak = WeakReference(activity)
        if (futureListener != null) {
            initializeController()
        }
    }


    private fun getSessionToken(): SessionToken {
        val activity = activity ?: throw  Exception("failed get Activity")
        return SessionToken(activity, ComponentName(activity, ExoPlayerService::class.java))
    }

    override val controller: MediaBrowser?
        get() = if (controllerFuture?.isDone == true) {
            controllerFuture?.get()
        } else {
            null
        }

    override fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null
    }

    override fun viewModelInitialize(listener: Runnable?) {
        futureListener = listener
        initializeController()
    }

    override fun removeViewModelListener() {
        futureListener = null
    }
    private fun initializeController() {
        if (controllerFuture != null) return
        controllerFuture =
            MediaBrowser.Builder(weak?.get()!!, getSessionToken()).buildAsync().apply {
                    futureListener?.let { addListener(it, MoreExecutors.directExecutor()) }
                }
    }

}