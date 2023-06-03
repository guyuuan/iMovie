package cn.chitanda.app.imovie

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 *@author: Chen
 *@createTime: 2022/11/20 14:34
 *@description:
 **/
@HiltAndroidApp
class IMovieApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(
                Timber.DebugTree()
            )
        }
    }
}