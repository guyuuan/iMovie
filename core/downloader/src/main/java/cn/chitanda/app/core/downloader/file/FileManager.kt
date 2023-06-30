package cn.chitanda.app.core.downloader.file

import cn.chitanda.app.core.downloader.extension.plus
import okio.FileSystem
import okio.ForwardingFileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.Sink
import okio.Source

/**
 * @author: Chen
 * @createTime: 2023/5/10 14:18
 * @description:
 **/
interface IDownloadFileManager {
    fun createFilePath(fileName: String, dir: String? = null): Path

}

abstract class DownloadFileManager(val basePath: Path, delegate: FileSystem) :
    ForwardingFileSystem(delegate), IDownloadFileManager {
    override fun sink(file: Path, mustCreate: Boolean): Sink {
        file.parent?.let(::createDirectories)
        return super.sink(file, mustCreate)
    }

    fun writeToFile(file: Path, source: Source) {
        val tmp = "$file.tmp".toPath()
        write(tmp) {
            writeAll(source)
        }
        this.atomicMove(tmp, file)
    }

    override fun createFilePath(fileName: String, dir: String?): Path {
        val dirPath = dir?.toPath()
        if (dirPath != null) {
            return if (dirPath.isAbsolute) {
                dirPath + fileName
            } else {
                basePath + dirPath + fileName
            }
        }
        return basePath + fileName
    }
}

class AndroidDownloadFileManager(basePath: String?) : DownloadFileManager(
    basePath?.toPath() ?: SYSTEM_TEMPORARY_DIRECTORY, SYSTEM
)