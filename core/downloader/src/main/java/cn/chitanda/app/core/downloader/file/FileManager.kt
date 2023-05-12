package cn.chitanda.app.core.downloader.file

import okio.Path

/**
 * @author: Chen
 * @createTime: 2023/5/10 14:18
 * @description:
 **/
interface DownloadFileManager {
    fun createFile(path: String): Path
}