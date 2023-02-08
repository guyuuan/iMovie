package cn.chitanda.app.imovie.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(vararg history: History)

    @Query("SELECT * FROM PLAY_HISTORY")
    fun getHistoryPagingSource(): PagingSource<Int,History>

    @Query("SELECT * FROM PLAY_HISTORY WHERE movieName LIKE :query")
    fun searchHistoryPagingSource(query:String):PagingSource<Int,History>

    @Delete
    suspend fun deleteHistory(vararg history: History)

    @Update
    suspend fun updateHistory(vararg history: History)

    @Query("SELECT * FROM PLAY_HISTORY WHERE movieId == :movieId")
    suspend fun findHistoryByMovieId(movieId:Long):History?
}