package cn.chitanda.app.imovie.core.data.repository.di

import cn.chitanda.app.imovie.core.data.repository.MoviesRepositoryImp
import cn.chitanda.app.imovie.core.data.repository.MoviesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *@author: Chen
 *@createTime: 2022/11/20 11:19
 *@description:
 **/
@Module
@InstallIn(SingletonComponent::class)
interface DataMoudle {

    @Binds
    @Singleton
    fun bindMovieRepository(
        moviesRepository: MoviesRepositoryImp
    ):MoviesRepository
}