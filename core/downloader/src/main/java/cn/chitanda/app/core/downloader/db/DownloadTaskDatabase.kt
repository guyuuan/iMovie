package cn.chitanda.app.core.downloader.db

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.chitanda.app.core.downloader.db.converter.DownloadStateConverter
import cn.chitanda.app.core.downloader.db.dao.ActualTaskDao
import cn.chitanda.app.core.downloader.db.dao.M3u8TaskDao
import cn.chitanda.app.core.downloader.db.entites.ActualTaskEntity
import cn.chitanda.app.core.downloader.db.entites.M3u8TaskEntity

/**
 * @author: Chen
 * @createTime: 2023/6/7 16:36
 * @description:
 **/
@Database(entities = [ActualTaskEntity::class, M3u8TaskEntity::class], version = 1)
@TypeConverters(DownloadStateConverter::class)
abstract class DownloadTaskDatabase : RoomDatabase() {
    abstract fun actualTaskDao(): ActualTaskDao
    abstract fun m3u8TaskDao(): M3u8TaskDao

    companion object {
        private var instance: DownloadTaskDatabase? = null

        @JvmStatic
        operator fun invoke(context: Context): DownloadTaskDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    DownloadTaskDatabase::class.java,
                    "task_db"
                ).build().also {
                    instance = it
                }
            }

        @JvmStatic
        @VisibleForTesting
        fun createTestDatabase(context: Context): DownloadTaskDatabase =
            Room.inMemoryDatabaseBuilder(context, DownloadTaskDatabase::class.java)
                .build()
    }
}