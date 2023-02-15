package cn.chitanda.app.imovie.feature.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.chitanda.app.imovie.core.data.repository.MoviesRepository
import cn.chitanda.app.imovie.core.model.Movie

/**
 * @author: Chen
 * @createTime: 2023/2/15 15:28
 * @description:
 **/
class SearchResultPagingSource(
    private val moviesRepository: MoviesRepository,
    private val searchKey: String
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            if (searchKey.isEmpty()){
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
            val next = params.key ?: 1
            val result = moviesRepository.searchMovie(searchKey, count = 10, page = next)
            LoadResult.Page(
                data = result.movies,
                prevKey = if (next == 1) null else next.minus(1),
                nextKey = if (next >= result.pgCount) null else next.plus(1)
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}