package cn.chitanda.app.imovie.feature.play.di

import cn.chitanda.app.imovie.core.media.MediaItemTree
import cn.chitanda.app.imovie.feature.play.service.MediaItemTreeImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *@author: Chen
 *@createTime: 2022/11/27 17:39
 *@description:
 **/
@Module
@InstallIn(SingletonComponent::class)
abstract class MediaItemTreeModule {

    @Binds
    @Singleton
    abstract fun bindsMediaItemTree(mediaItemTree: MediaItemTreeImpl): MediaItemTree
}