package cn.chitanda.app.imovie.core.model

import kotlinx.datetime.LocalTime

/**
 * @author: Chen
 * @createTime: 2023/2/6 16:31
 * @description:
 **/
data class HistoryResource(
    val id: Long = 0,
    val movieId: Long,
    val movieName: String,
    val duration: Long,
    val position: Long,
    val moviePic: String,
    val updateTime: Long,
    val index: Int,
    val indexName: String,
) {
    val lastSeen = with(LocalTime.fromMillisecondOfDay(position.toInt())) {
        "${
            if (hour > 0) {
                hour.toString().padStart(2, '0') + ":"
            } else {
                ""
            }
        }${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}"
    }
}
