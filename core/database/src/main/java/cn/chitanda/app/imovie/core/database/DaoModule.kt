package cn.chitanda.app.imovie.core.database

import cn.chitanda.app.imovie.core.database.dao.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author: Chen
 * @createTime: 2023/2/1 15:35
 * @description:
 **/
@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao = appDatabase.historyDao()
}