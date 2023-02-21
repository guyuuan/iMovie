package cn.chitanda.app.imovie.core.network.retrofit

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
    suspend fun download(@Url url: String): ResponseBody
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
            emit(DownloadState.Downloading(0f))
            val tempFile = File("$savePath.tmp")
            val response = networkApi.download(url)
            response.byteStream().use { input ->
                tempFile.outputStream().use { output ->
                    val total = response.contentLength()
                    var saved = 0L
                    val buffer = ByteArray(8 * 1024)
                    var len = input.read(buffer)
                    while (len >= 0) {
                        output.write(buffer, 0, len)
                        saved += len
                        emit(DownloadState.Downloading((saved / total.toFloat())))
                        len = input.read(buffer)
                    }
                }
            }
            tempFile.renameTo(File(savePath))
            emit(DownloadState.Finish)
        }.catch {
            emit(DownloadState.Failed(it))
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

}