package cn.chitanda.app.imovie.core

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:09
 * @description:
 **/
sealed class DownloadState {
    class Downloading(val progress: Float) : DownloadState()
    class Failed(val error: Throwable) : DownloadState()

    object Finish : DownloadState()
}
