package cn.chitanda.app.core.downloader.db.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.chitanda.app.core.downloader.task.DownloadTaskState

/**
 * @author: Chen
 * @createTime: 2023/6/7 14:53
 * @description:
 **/
@Entity(tableName = "m3u8_task")
data class M3u8TaskEntity(
    val originUrl: String,
    val createTime: Long,
    val updateTime: Long,
    val coverImageUrl: String?,
    val state: DownloadTaskState,
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val taskId: String
)