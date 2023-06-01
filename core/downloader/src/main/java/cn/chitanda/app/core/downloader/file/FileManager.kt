package cn.chitanda.app.core.downloader.file

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
    fun createFilePath(fileName: String, dir: String?): Path

}

abstract class DownloadFileManager(delegate: FileSystem) : ForwardingFileSystem(delegate),
    IDownloadFileManager {
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
}