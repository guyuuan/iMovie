package cn.chitanda.app.core.downloader.extension

import okio.Path
import okio.Path.Companion.toPath


/**
 * @author: Chen
 * @createTime: 2023/6/5 10:25
 * @description:
 **/

operator fun Path.plus(other: Path): Path {
    return "$this${Path.DIRECTORY_SEPARATOR}$other".toPath()
}

operator fun Path.plus(other: String): Path {
    return "$this${Path.DIRECTORY_SEPARATOR}$other".toPath()
}