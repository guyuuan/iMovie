package cn.chitanda.app.imovie.core.model

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
    val moviePic: String,
    val updateTime: Long,
    val index:Int,
)
