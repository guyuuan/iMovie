package cn.chitanda.app.core.downloader.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @author: Chen
 * @createTime: 2023/5/31 15:49
 * @description:
 **/

internal interface IDownloadNetwork {

    @Streaming
    @GET
    suspend fun download(@Url url: String): Response<ResponseBody>
}

internal class DownloadNetwork private constructor(delegate: IDownloadNetwork) :
    IDownloadNetwork by delegate {

    companion object {
        private var instance: IDownloadNetwork? = null
        operator fun invoke(): IDownloadNetwork {
            return instance ?: synchronized(this) {
                instance ?: (Retrofit.Builder().baseUrl("https://chitanda.cn").build()
                    .create(IDownloadNetwork::class.java)).also { instance = it }
            }
        }
    }
}
