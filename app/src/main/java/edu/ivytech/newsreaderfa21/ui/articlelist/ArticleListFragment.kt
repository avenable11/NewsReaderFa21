package edu.ivytech.newsreaderfa21.ui.articlelist

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ivytech.newsreaderfa21.Article
import edu.ivytech.newsreaderfa21.R
import edu.ivytech.newsreaderfa21.databinding.ArticleListFragmentBinding
import edu.ivytech.newsreaderfa21.databinding.ListItemBinding

class ArticleListFragment : Fragment() {

    companion object {
        fun newInstance() = ArticleListFragment()
    }
    private lateinit var binding : ArticleListFragmentBinding
    private lateinit var viewModel: ArticleListViewModel
    private var adapter : ArticleAdapter = ArticleAdapter(emptyList())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(ArticleListViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ArticleListFragmentBinding.inflate(inflater, container, false)
        binding.articleList.layoutManager = LinearLayoutManager(context)
        binding.articleList.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.articleListLiveData.observe(viewLifecycleOwner,{
            articles -> adapter = ArticleAdapter(articles)
            binding.articleList.adapter = adapter
        })
    }

    private inner class ArticleViewHolder(val itemBinding:ListItemBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {
            private lateinit var article : Article
            fun bind(article:Article) {
                this.article = article
                itemBinding.listArticleTitle.text = article.title
                itemBinding.listPubDate.text = DateFormat.format("EEEE, MMM, dd, yyyy", article.pubDate).toString()
            }
        }

    private inner class ArticleAdapter(val articles : List<Article>) : RecyclerView.Adapter<ArticleViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
            val itemBinding : ListItemBinding = ListItemBinding.inflate(layoutInflater,parent, false)
            return ArticleViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
            holder.bind(articles[position])
        }

        override fun getItemCount(): Int  = articles.size

    }

}