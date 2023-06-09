package cn.chitanda.app.core.downloader.db.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import cn.chitanda.app.core.downloader.task.DownloadTaskState

/**
 * @author: Chen
 * @createTime: 2023/6/7 14:58
 * @description:
 **/
@Entity(
    tableName = "actual_task",
    foreignKeys = [
        ForeignKey(
            entity = M3u8TaskEntity::class,
            parentColumns = arrayOf("task_id"),
            childColumns = arrayOf("parent_task_id"),
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class ActualTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originUrl: String,
    val createTime: Long,
    val updateTime: Long = createTime,
    @ColumnInfo(name = "parent_task_id", index = true) val parentTaskId: String,
    val state: DownloadTaskState,
    val fileName: String,
    val downloadDir: String,
)
