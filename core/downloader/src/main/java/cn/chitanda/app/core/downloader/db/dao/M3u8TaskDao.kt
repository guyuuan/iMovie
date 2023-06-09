package cn.chitanda.app.core.downloader.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import cn.chitanda.app.core.downloader.db.entites.M3u8TaskEntity

/**
 * @author: Chen
 * @createTime: 2023/6/7 16:12
 * @description:
 **/
@Dao
interface M3u8TaskDao {
    @Insert
    suspend fun insertM3u8Task(task: M3u8TaskEntity)

    @Update
    suspend fun updateM3u8Task(task: M3u8TaskEntity)


    @Delete
    suspend fun deleteM3u8Task(task: M3u8TaskEntity)
}