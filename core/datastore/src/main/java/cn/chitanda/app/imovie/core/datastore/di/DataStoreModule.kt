package cn.chitanda.app.imovie.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import cn.chitanda.app.imovie.core.datastore.AppPreferencesDataSource
import cn.chitanda.app.imovie.core.datastore.UserPreferencesOuterClass.UserPreferences
import cn.chitanda.app.imovie.core.datastore.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * @author: Chen
 * @createTime: 2023/3/13 16:08
 * @description:
 **/
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context,
        serializer: UserPreferencesSerializer
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        ){
            context.dataStoreFile("user_preferences.pb")
        }
    }
//    @Singleton
//    @Provides
//    fun provideAppPreferences(
//        userPreferences:DataStore<UserPreferences>
//    ): AppPreferencesDataSource {
//        return AppPreferencesDataSource(userPreferences)
//    }
}