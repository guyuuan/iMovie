package cn.chitanda.app.imovie.core.network.retrofit

import android.util.Log
import cn.chitanda.app.imovie.core.DownloadState
import cn.chitanda.app.imovie.core.model.GithubRelease
import cn.chitanda.app.imovie.core.model.Movie
import cn.chitanda.app.imovie.core.model.MovieSearchResult
import cn.chitanda.app.imovie.core.network.AppNetworkDataSource
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 *@author: Chen
 *@createTime: 2022/11/19 15:16
 *@description:
 **/
private interface RetrofitAppNetworkApi {

    @FormUrlEncoded
    @POST("/category/{type}")
    suspend fun getMovieListByType(
        @Path("type") type: Int, @Field("num") count: Int, @Field("pg") page: Int,
    ): MovieSearchResult

    @FormUrlEncoded
    @POST("/search")
    suspend fun searchMovie(
        @Field("keyword") keyword: String, @Field("num") count: Int, @Field("pg") page: Int,
    ): MovieSearchResult

    @GET("/play/{id}")
    suspend fun getMovieDetail(@Path("id") id: Long): Movie

    @GET("https://api.github.com/repos/cjchen98/imovie/releases/latest")
    suspend fun getAppLatestVersion(): GithubRelease

    @GET
    @Streaming
    suspend fun download(@Url url: String): Response<ResponseBody>
}

private const val AppBaseUrl = "https://tv.chitanda.cn"

@Singleton
class RetrofitAppNetwork @Inject constructor(
    networkJson: Json,
) : AppNetworkDataSource {

    private val networkApi = Retrofit.Builder().baseUrl(AppBaseUrl)
        .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }).build()).addConverterFactory(
            @OptIn(ExperimentalSerializationApi::class) networkJson.asConverterFactory(
                "application/json".toMediaType()
            )
        ).build().create(RetrofitAppNetworkApi::class.java)

    override suspend fun getMoviesByType(type: Int, count: Int, page: Int): List<Movie> =
        withContext(Dispatchers.IO) {
            networkApi.getMovieListByType(type, count, page).movies
        }

    override suspend fun getMovieDetail(id: Long): Movie = withContext(Dispatchers.IO) {
        networkApi.getMovieDetail(id)
    }

    override suspend fun searchMovie(key: String, count: Int, page: Int): MovieSearchResult =
        withContext(Dispatchers.IO) {
            networkApi.searchMovie(key, count, page)
        }

    override suspend fun getAppLatestVersion(): GithubRelease =
        withContext(Dispatchers.IO) {
            networkApi.getAppLatestVersion()
        }

    override fun download(url: String, savePath: String): Flow<DownloadState> =
        flow {
            emit(DownloadState.Downloading(0))
            val response = networkApi.download(url)
            val filename = response.headers()["content-disposition"]?.split(";")
                ?.find { s ->
                    s.startsWith("filename=")
                }?.replace("filename=", "")
                ?: url.split("/").last()
            val filePath = "$savePath/$filename"
            val tempFile = File("$filePath.tmp")
            if(tempFile.exists()){
                tempFile.delete()
            }
            response.body()?.byteStream()?.use { input ->
                tempFile.outputStream().use { output ->
                    val total = response.body()!!.contentLength()
                    var saved = 0L
                    val buffer = ByteArray(8 * 1024)
                    var len = input.read(buffer)
                    while (len >= 0) {
                        output.write(buffer, 0, len)
                        saved += len
                        emit(
                            DownloadState.Downloading(
                                ((saved / total.toFloat()) * 100).roundToInt().coerceAtMost(100)
                            )
                        )
                        len = input.read(buffer)
                    }
                }
            }
            tempFile.renameTo(File(filePath))
            emit(DownloadState.Finish(filePath))
        }.catch {
            Log.e(TAG, "download: ", it)
            emit(DownloadState.Failed(it))
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

}

private const val TAG = "RetrofitAppNetwork"