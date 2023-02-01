package cn.chitanda.app.imovie.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.chitanda.app.imovie.core.database.dao.HistoryDao
import cn.chitanda.app.imovie.core.database.model.History

/**
 * @author: Chen
 * @createTime: 2023/2/1 15:07
 * @description:
 **/
@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

}