package kr.ac.kumoh.s20160250.bookreviewer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kr.ac.kumoh.s20160250.bookreviewer.dao.HistoryDao
import kr.ac.kumoh.s20160250.bookreviewer.dao.ReviewDao
import kr.ac.kumoh.s20160250.bookreviewer.model.History
import kr.ac.kumoh.s20160250.bookreviewer.model.Review

@Database(entities = [History::class, Review::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

fun getAppDatabase(context: Context): AppDatabase {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'REVIEW'(id INTEGER,'review' TEXT," + "PRIMARY KEY('id'))")
        }
    }
    return Room.databaseBuilder(
        context, AppDatabase::class.java,
        "BookSearchDB"
    ).addMigrations(MIGRATION_1_2)
        .build()
}