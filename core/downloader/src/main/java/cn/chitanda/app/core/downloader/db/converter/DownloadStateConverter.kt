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
            DownloadTaskState.Downloading.ordinal -> DownloadTaskState.Downloading
            DownloadTaskState.Pause.ordinal -> DownloadTaskState.Pause
            DownloadTaskState.Completed.ordinal -> DownloadTaskState.Completed
            DownloadTaskState.Initially.ordinal -> DownloadTaskState.Initially
            DownloadTaskState.Failed.ordinal -> DownloadTaskState.Failed
            DownloadTaskState.Pending.ordinal -> DownloadTaskState.Pending
            else -> error("$value can't convert to DownloadTaskState")
        }
    }

    @TypeConverter
    fun downloadTaskStateToInt(value: DownloadTaskState): Int = value.ordinal
}