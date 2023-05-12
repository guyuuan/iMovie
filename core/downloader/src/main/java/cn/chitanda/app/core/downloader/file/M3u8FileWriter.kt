package cn.chitanda.app.core.downloader.file

import cn.chitanda.app.core.downloader.decrypt.getCipher
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.source
import java.io.InputStream
import javax.crypto.CipherInputStream

/**
 * @author: Chen
 * @createTime: 2023/5/10 16:36
 * @description:
 **/
class M3u8FileWriter(
    private val file: Path, input: InputStream,
    key: ByteArray?, method: String?,
) : AutoCloseable {
    private val input = if (key == null || method == null) input else CipherInputStream(
        input, getCipher(key, method)
    )

    fun write(onProcess: (Long) -> Unit) {

        FileSystem.SYSTEM.sink(file).buffer().use { sink ->
            var length = 0L
            while (input.source().buffer().read(sink.buffer, 1024L)
                    .takeIf { it != -1L }?.also { count -> length += count } != null
            ) {
                sink.flush()
                onProcess.invoke(length)
            }
        }
    }

    override fun close() {
        input.close()
    }
}
