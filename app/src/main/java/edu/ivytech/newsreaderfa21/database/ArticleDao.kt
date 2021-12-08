package edu.ivytech.newsreaderfa21.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.ivytech.newsreaderfa21.Article
import java.util.*

@Dao
interface ArticleDao {
    @Query("Select * from Article")
    fun getArticles() : LiveData<List<Article>>

    @Query("Select * from Article where uuid = :id")
    fun getArticle(id : UUID) : LiveData<Article?>

    @Query("Delete from Article")
    fun deleteArticles()

    @Query("Delete from ArticleHistory")
    fun deleteHistory()

    @Query("insert into ArticleHistory select * from article")
    fun copyHistory()

    @Query("Select * from Article where title not in (select title from ArticleHistory)")
    fun getNewArticles() : List<Article>

    @Insert
    fun insertArticle(article : Article)
}