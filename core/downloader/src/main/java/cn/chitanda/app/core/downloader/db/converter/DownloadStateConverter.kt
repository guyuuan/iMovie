package cn.chitanda.app.core.downloader.db.converter

import androidx.room.TypeConverter
import cn.chitanda.app.core.downloader.task.DownloadTaskState

/**
 * @author: Chen
 * @createTime: 2023/6/7 16:58
 * @description:
 **/
class DownloadStateConverter {
    @TypeConverter
    fun intToDownloadTaskState(value: Int): DownloadTaskState {
        return when (value) {
            DownloadTaskState.downloading.ordinal -> DownloadTaskState.downloading
            DownloadTaskState.pause.ordinal -> DownloadTaskState.pause
            DownloadTaskState.completed.ordinal -> DownloadTaskState.completed
            DownloadTaskState.initially.ordinal -> DownloadTaskState.initially
            DownloadTaskState.failed.ordinal -> DownloadTaskState.failed
            DownloadTaskState.pending.ordinal -> DownloadTaskState.pending
            else -> error("$value can't convert to DownloadTaskState")
        }
    }

    @TypeConverter
    fun downloadTaskStateToInt(value: DownloadTaskState): Int = value.ordinal
}