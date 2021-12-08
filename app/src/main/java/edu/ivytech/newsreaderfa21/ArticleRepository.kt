package edu.ivytech.newsreaderfa21

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import androidx.room.Room
import edu.ivytech.newsreaderfa21.api.*
import edu.ivytech.newsreaderfa21.database.ArticleDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val DATABASE_NAME = "NewsReader.db"
private const val TAG = "Article Repo"
class ArticleRepository private constructor(private val context : Context) {
    private val database : ArticleDatabase = Room.databaseBuilder(context,
        ArticleDatabase::class.java,
        DATABASE_NAME).build()
    private val articleDao = database.articleDao()
    private val executor : Executor = Executors.newSingleThreadExecutor()
    private val nytApi : NYTApi
    private val cnnApi : CNNApi
    init {
        val retrofitNYT:Retrofit = Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/topstories/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        nytApi = retrofitNYT.create(NYTApi::class.java)
        val retrofitCNN : Retrofit = Retrofit.Builder()
            .baseUrl("http://rss.cnn.com/rss/")
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        cnnApi = retrofitCNN.create(CNNApi::class.java)
    }

    fun fetchArticles() {
        var responseData : List<Article> = mutableListOf()
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        var source = prefs.getString("source", "nyt")
        if(source == null)
            source = "nyt"
        if(source == "nyt") {
            val nytRequest: Call<NYTResponse> = nytApi.downloadArticles()
            nytRequest.enqueue(object : Callback<NYTResponse> {
                override fun onResponse(call: Call<NYTResponse>, response: Response<NYTResponse>) {
                    Log.d(TAG, "Response Received")
                    val nytResponse: NYTResponse? = response.body()
                    val articles: List<NYTArticle>? = nytResponse?.results
                    if (articles != null) {
                        for (a in articles) {
                            val date =
                                SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ssZ", Locale.US).parse(a.date)
                            val article = Article(a.title, date, a.url, a.description)
                            responseData += article
                        }
                        insertArticles(responseData)
                    } else {
                        Log.d(TAG, "Response is coming back null")
                    }

                }

                override fun onFailure(call: Call<NYTResponse>, t: Throwable) {
                    Log.e(TAG, "Failed to fetch articles", t)
                }

            })
        } else {
            val cnnRequest: Call<CNNResponse> = cnnApi.downloadArticles()
            cnnRequest.enqueue(object : Callback<CNNResponse> {
                override fun onResponse(call: Call<CNNResponse>, response: Response<CNNResponse>) {
                    val cnnResponse: CNNResponse? = response.body()
                    val articles: List<CNNArticle>? = cnnResponse?.articles
                    if (articles != null) {
                        for (a in articles) {
                            val date = SimpleDateFormat(
                                "EEE, dd MMM yyyy HH:mm:ss 'GMT'",
                                Locale.US
                            ).parse(a.date)
                            val article = Article(a.title, date, a.link, a.description)
                            responseData += article
                        }
                        insertArticles(responseData)
                    } else {
                        Log.d(TAG, "Response is coming back null")
                    }

                }

                override fun onFailure(call: Call<CNNResponse>, t: Throwable) {
                    Log.e("Article Repo", "Failed to fetch articles", t)
                }

            })
        }
    }

    private fun insertArticles(responseData: List<Article>) {
        changeHistory()
        for (a in responseData){
            insertArticle(a)
        }

    }

    fun getArticles() : LiveData<List<Article>> = articleDao.getArticles()
    fun getArticle(id : UUID) : LiveData<Article?> = articleDao.getArticle(id)
    fun getNewArticles() : List<Article> = articleDao.getNewArticles()
    fun changeHistory() {
        executor.execute{
            articleDao.deleteHistory()
            articleDao.copyHistory()
            articleDao.deleteArticles()
        }
    }
    fun insertArticle(article: Article)
    {
        executor.execute{
            articleDao.insertArticle(article)
        }
    }

    companion object {
        private var INSTANCE : ArticleRepository? = null

        fun initialize(context: Context) {
            INSTANCE = ArticleRepository(context)
        }

        fun get() : ArticleRepository {
            return INSTANCE?: throw IllegalStateException("Article Repository msut be initialized.")
        }
    }
}