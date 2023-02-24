package cn.chitanda.app.imovie.core

/**
 * @author: Chen
 * @createTime: 2023/2/21 17:09
 * @description:
 **/
sealed class DownloadState {
    abstract val progress: Int

    data class Downloading(override val progress: Int) : DownloadState()
    data class Failed(val error: Throwable, override val progress: Int=0) : DownloadState()

    data class Finish(val file:String) : DownloadState() {
        override val progress: Int = 100
    }
}
