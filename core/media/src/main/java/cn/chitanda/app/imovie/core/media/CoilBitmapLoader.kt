package cn.chitanda.app.imovie.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.media3.session.BitmapLoader
import coil.executeBlocking
import coil.imageLoader
import coil.request.ImageRequest
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Executors

/**
 * @author: Chen
 * @createTime: 2023/2/2 18:26
 * @description:
 **/
class CoilBitmapLoader(private val context: Context) :
    BitmapLoader {
    private val imageLoader = context.imageLoader
    private val executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor())
    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> {
        return executor.submit<Bitmap> { decode(data) }
    }

    private fun decode(data: ByteArray): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        check(bitmap != null) {
            "Could not decode image data"
        }
        return bitmap
    }

    override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> {
        return executor.submit<Bitmap> { load(uri) }
    }

    private fun load(uri: Uri): Bitmap {
        val request = imageLoader.executeBlocking(
            ImageRequest.Builder(context).data(uri).build()
        )
        val bitmap = (request.drawable as? BitmapDrawable)?.bitmap
        check(bitmap != null) {
            "Could not load bitmap from $uri"
        }
        return bitmap
    }
}