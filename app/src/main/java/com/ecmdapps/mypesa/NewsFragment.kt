package com.ecmdapps.mypesa

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.prof.rssparser.Article
import kotlinx.android.synthetic.main.content_news.*

class NewsFragment : Fragment() {
    private lateinit var newsSource: NewsSource
    private var articleList = ArrayList<Article>()
    private lateinit var newsAdapter : NewsAdapter


    companion object {
        fun newInstance(): NewsFragment {
            return NewsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsSource = NewsSource(context!!)
        newsAdapter = NewsAdapter(articleList, context!!)

        loadNews()

        newsRecyclerView.adapter = newsAdapter
        newsRecyclerView.layoutManager = LinearLayoutManager(context)
        refresh.setOnRefreshListener { loadNews(); refresh.isRefreshing = false }
    }

    private fun loadNews() {
        newsSource.loadNews()
    }

    fun reload() {
        loadNews()
    }

    fun displayArticles(newArticleList: ArrayList<Article>) {
        articleList.addAll(newArticleList)
        newsAdapter.notifyDataSetChanged()
    }

    fun clearNewsList(){
        articleList.clear()
        newsAdapter.notifyDataSetChanged()
    }
}