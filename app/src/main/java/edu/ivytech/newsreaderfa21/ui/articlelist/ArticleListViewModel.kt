package edu.ivytech.newsreaderfa21.ui.articlelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import edu.ivytech.newsreaderfa21.Article
import edu.ivytech.newsreaderfa21.ArticleRepository

class ArticleListViewModel : ViewModel() {
    private val articleRepository : ArticleRepository = ArticleRepository.get()
    val articleListLiveData : LiveData<List<Article>> = articleRepository.getArticles()
}