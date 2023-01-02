@file:OptIn(ExperimentalSerializationApi::class)

package cn.chitanda.app.imovie.core.network.di

import cn.chitanda.app.imovie.core.network.AppNetworkDataSource
import cn.chitanda.app.imovie.core.network.retrofit.RetrofitAppNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 *@author: Chen
 *@createTime: 2022/11/19 16:03
 *@description:
 **/
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }


    @Provides
    @Singleton
    fun provideNetworkDataSource(json: Json): AppNetworkDataSource = RetrofitAppNetwork(json)
}