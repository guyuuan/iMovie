package cn.chitanda.app.imovie.di

import cn.chitanda.app.imovie.core.media.AppMediaController
import cn.chitanda.app.imovie.media.AppMediaControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *@author: Chen
 *@createTime: 2022/12/8 19:45
 *@description:
 **/
@Module
@InstallIn(SingletonComponent::class)
object AppMediaControllerModule {
    @Provides
    @Singleton
    fun bindAppMediaControllerImpl(): AppMediaControllerImpl = AppMediaControllerImpl()

    @Provides
    @Singleton
    fun provideMediaController(impl: AppMediaControllerImpl): AppMediaController = impl
}