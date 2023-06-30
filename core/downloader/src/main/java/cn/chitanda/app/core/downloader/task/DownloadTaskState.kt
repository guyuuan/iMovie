package cn.chitanda.app.core.downloader.task

enum class DownloadTaskState {
    Downloading, Completed, Initially, Parsed, Failed, Merging, Pending, Pause,
}