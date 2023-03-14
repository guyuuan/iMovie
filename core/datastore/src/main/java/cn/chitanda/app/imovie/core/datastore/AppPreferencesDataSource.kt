package cn.chitanda.app.imovie.core.datastore

import androidx.datastore.core.DataStore
import cn.chitanda.app.imovie.core.datastore.UserPreferencesOuterClass.UserPreferences
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author: Chen
 * @createTime: 2023/3/13 17:00
 * @description:
 **/
@Singleton
class AppPreferencesDataSource @Inject constructor(private val userPreferencesDataStore: DataStore<UserPreferences>) {
    val enablePip = userPreferencesDataStore.data.map {
        it.usePip
    }

    suspend fun setUsePip(enabled: Boolean) {
        userPreferencesDataStore.updateData {
            it.copy {
                usePip = enabled
            }
        }
    }
}