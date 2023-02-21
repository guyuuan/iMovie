package cn.chitanda.app.imovie.core.network

import cn.chitanda.app.imovie.core.DownloadState
import cn.chitanda.app.imovie.core.model.GithubRelease
import cn.chitanda.app.imovie.core.model.Movie
import cn.chitanda.app.imovie.core.model.MovieSearchResult
import kotlinx.coroutines.flow.Flow

/**
 *@author: Chen
 *@createTime: 2022/11/19 14:49
 *@description:
 **/
interface AppNetworkDataSource {

    suspend fun getMoviesByType(type: Int, count: Int, page: Int): List<Movie>

    suspend fun getMovieDetail(id: Long): Movie

    suspend fun searchMovie(key: String, count: Int, page: Int): MovieSearchResult

    suspend fun getAppLatestVersion():GithubRelease

    fun download(url:String, savePath:String):Flow<DownloadState>
}