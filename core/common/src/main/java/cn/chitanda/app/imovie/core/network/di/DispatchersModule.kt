package cn.chitanda.app.imovie.core.network.di

import cn.chitanda.app.imovie.core.network.AppDispatchers.IO
import cn.chitanda.app.imovie.core.network.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 *@author: Chen
 *@createTime: 2022/11/19 13:24
 *@description:
 **/
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}