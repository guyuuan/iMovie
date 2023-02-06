package cn.chitanda.app.imovie.core.data.repository

import cn.chitanda.app.imovie.core.model.HomeData
import cn.chitanda.app.imovie.core.model.Movie
import cn.chitanda.app.imovie.core.network.AppNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 *@author: Chen
 *@createTime: 2022/11/19 16:42
 *@description:
 **/
interface MoviesRepository {
    fun getHomePageData(): Flow<HomeData>

    fun getMoviesByType(type: Int, count: Int, page: Int): Flow<List<Movie>>

    fun searchMovie(key: String, count: Int, page: Int): Flow<List<Movie>>

    fun getMovieDetail(id: Long): Flow<Movie>
}

class MoviesRepositoryImp @Inject constructor(
    private val dataSource: AppNetworkDataSource
) : MoviesRepository {
    override fun getHomePageData() =
        combine(
            getMoviesByType(1, 8, 1),
            getMoviesByType(2, 8, 1),
            getMoviesByType(3, 8, 1)
        ) { movies, anime, drama ->
            HomeData(movies, anime, drama)
        }

    override fun getMoviesByType(type: Int, count: Int, page: Int) = flow {
        emit(dataSource.getMoviesByType(type, count, page))
    }

    override fun searchMovie(key: String, count: Int, page: Int) = flow {
        emit(dataSource.searchMovie(key, count, page))
    }

    override fun getMovieDetail(id: Long) = flow {
        emit(dataSource.getMovieDetail(id))
    }

}