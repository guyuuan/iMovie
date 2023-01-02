package cn.chitanda.app.imovie.core.decoder.di

import cn.chitanda.app.imovie.core.decoder.StringDecoder
import cn.chitanda.app.imovie.core.decoder.UriDecoder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 *@author: Chen
 *@createTime: 2022/11/22 17:11
 *@description:
 **/
@Module
@InstallIn(SingletonComponent::class)
interface StringDecoderModule {
    @Binds
    abstract fun bindStringDecoder(uriDecoder: UriDecoder): StringDecoder
}