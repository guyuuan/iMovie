package cn.chitanda.app.imovie.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.chitanda.app.imovie.core.database.model.History
import kotlinx.coroutines.flow.Flow

/**
 * @author: Chen
 * @createTime: 2023/2/1 15:20
 * @description:
 **/
@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(vararg history: History)

    @Query("SELECT * FROM PLAY_HISTORY")
    fun gethistoryFlow(): Flow<List<History>>

    @Delete
    fun deleteHistory(vararg history: History)

    @Update
    fun updateHistory(vararg history: History)
}