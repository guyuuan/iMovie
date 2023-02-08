package cn.chitanda.app.imovie.core.data.repository

import androidx.paging.PagingSource
import cn.chitanda.app.imovie.core.database.dao.HistoryDao
import cn.chitanda.app.imovie.core.database.model.History
import cn.chitanda.app.imovie.core.model.HistoryResource
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 2023/2/6 15:58
 * @description:
 **/
interface HistoryRepository {
    suspend fun insertHistory(vararg histories: HistoryResource)

    suspend fun deleteHistory(vararg histories: HistoryResource)

    suspend fun updateHistory(vararg histories: HistoryResource)

    suspend fun findHistoryById(movieId: Long): HistoryResource?

    fun getHistoryPagingSource(): PagingSource<Int, History>
    fun getSearchHistoryPagingSource(query: String): PagingSource<Int, History>
}

class HistoryRepositoryImp @Inject constructor(private val dao: HistoryDao) : HistoryRepository {
    override suspend fun insertHistory(vararg histories: HistoryResource) {
        dao.insertHistory(* (histories.map { it.asHistory() }.toTypedArray()))
    }

    override suspend fun deleteHistory(vararg histories: HistoryResource) {
        dao.deleteHistory(* (histories.map { it.asHistory() }.toTypedArray()))
    }

    override suspend fun updateHistory(vararg histories: HistoryResource) {
        dao.updateHistory(* (histories.map { it.asHistory() }.toTypedArray()))
    }

    override suspend fun findHistoryById(movieId: Long): HistoryResource? {
        return dao.findHistoryByMovieId(movieId)?.asHistoryResource()
    }

    override fun getHistoryPagingSource() = dao.getHistoryPagingSource()


    override fun getSearchHistoryPagingSource(query: String) =
        dao.searchHistoryPagingSource("%$query%")
}

fun HistoryResource.asHistory(): History =
    History(id, movieId, movieName, duration, position, moviePic, updateTime, index, indexName)

fun History.asHistoryResource(): HistoryResource = HistoryResource(
    id, movieId, movieName, duration, position, moviePic, updateTime, index, indexName
)