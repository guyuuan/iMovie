package cn.chitanda.app.imovie.core.network

import cn.chitanda.app.imovie.core.module.Movie

/**
 *@author: Chen
 *@createTime: 2022/11/19 14:49
 *@description:
 **/
interface AppNetworkDataSource {

    suspend fun getMoviesByType(type: Int, count: Int, page: Int): List<Movie>

    suspend fun getMovieDetail(id: Long): Movie

    suspend fun searchMovie(key: String, count: Int, page: Int): List<Movie>
}