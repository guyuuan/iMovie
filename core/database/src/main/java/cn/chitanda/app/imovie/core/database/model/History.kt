package cn.chitanda.app.imovie.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author: Chen
 * @createTime: 2023/2/1 15:09
 * @description:
 **/
@Entity(
    tableName = "play_history"
)
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val movieId: Long,
    val movieName: String,
    val duration: Long,
    val position: Long,
    val moviePic: String,
    val updateTime: Long,
    val index: Int,
    val indexName:String,
)
