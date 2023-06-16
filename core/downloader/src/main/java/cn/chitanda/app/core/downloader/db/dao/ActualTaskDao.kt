package cn.chitanda.app.core.downloader.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cn.chitanda.app.core.downloader.db.entites.ActualTaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * @author: Chen
 * @createTime: 2023/6/7 16:13
 * @description:
 **/

@Dao
interface ActualTaskDao {
    @Insert
    suspend fun insertActualTask(task: ActualTaskEntity):Long

    @Update
    suspend fun updateActualTask(task: ActualTaskEntity)

    @Query("SELECT COUNT(*) FROM actual_task WHERE parent_task_id == :taskId AND state == 1")
    fun getCompletedActualTasksCountFlow(taskId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM actual_task WHERE parent_task_id == :taskId AND state != 1 AND state !=4")
    fun getUndoneActualTasksCount(taskId: String): Int
}