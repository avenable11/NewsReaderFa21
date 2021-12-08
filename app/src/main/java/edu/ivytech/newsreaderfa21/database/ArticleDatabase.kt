package edu.ivytech.newsreaderfa21.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.ivytech.newsreaderfa21.Article
import edu.ivytech.newsreaderfa21.ArticleHistory


@Database(entities=[Article::class, ArticleHistory::class], version = 1)
@TypeConverters(ArticleTypeConverters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun articleDao() : ArticleDao
}

