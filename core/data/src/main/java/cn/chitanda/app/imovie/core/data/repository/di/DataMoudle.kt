package cn.chitanda.app.imovie.core.data.repository.di

import cn.chitanda.app.imovie.core.data.repository.AppVersionRepository
import cn.chitanda.app.imovie.core.data.repository.AppVersionRepositoryImp
import cn.chitanda.app.imovie.core.data.repository.HistoryRepository
import cn.chitanda.app.imovie.core.data.repository.HistoryRepositoryImp
import cn.chitanda.app.imovie.core.data.repository.MoviesRepository
import cn.chitanda.app.imovie.core.data.repository.MoviesRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
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
    ): MoviesRepository

    @Binds
    @Singleton
    fun bindHistoryRepository(
        historyRepository: HistoryRepositoryImp
    ): HistoryRepository

}

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelMoudle {
    @Binds
    @ViewModelScoped
    fun bindAppVersionRepository(appVersionRepository: AppVersionRepositoryImp): AppVersionRepository
}