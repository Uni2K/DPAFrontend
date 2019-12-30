package com.project.app.Paging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.project.app.Helpers.Constants
import com.project.app.Objects.ContentModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Database(
    entities = [ContentModel::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OfflineDatabase : RoomDatabase() {
    companion object {
        fun create(context: Context, useInMemory : Boolean): OfflineDatabase{
            val databaseBuilder = if(useInMemory) {
                Room.inMemoryDatabaseBuilder(context, OfflineDatabase::class.java)
            } else {
                Room.databaseBuilder(context, OfflineDatabase::class.java, "content.db")
            }

            val db=databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
            //     db.clearAllTables()  //DEBUG
            clearUnusedData(db)


            return db
        }

        private fun clearUnusedData(db: OfflineDatabase) {
            GlobalScope.launch {

                db.contentAccessor().deleteByContentName(Constants.CONTENT_SEARCH)
                db.contentAccessor().deleteByContentName(Constants.CONTENT_USER_VOTED)
                db.contentAccessor().deleteByContentName(Constants.CONTENT_USER_ASKED)
                db.contentAccessor().deleteByContentName(Constants.CONTENT_USER_FOLLOWING)


            }
        }
    }

    abstract fun contentAccessor(): ContentDAO

}
