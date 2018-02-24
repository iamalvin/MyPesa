package com.ecmdapps.mypesa

import android.content.Context
import android.util.Log
import com.prof.rssparser.Article
import com.prof.rssparser.Parser


class NewsSource(private val context: Context) {
    fun loadNews() {
        val newsSources = ArrayList<String>()
        newsSources.add("https://bitcoinmagazine.com/feed/")
        newsSources.add("http://feeds.feedburner.com/CoinDesk?format=xml")
        newsSources.add("https://news.bitcoin.com/feed/")
        newsSources.add("https://www.ccn.com/news/feed/")
        newsSources.add("https://www.newsbtc.com/feed/")
        newsSources.add("https://cryptocurrencynews.com/feed/")

        for (i in 0 until newsSources.size){
            if (i == 0){
                resetList()
            }
            getFeed(newsSources[i])
        }
    }

    private fun resetList() {
        val act = context as MainActivity
        (act.mainAdapter!!.getRegisteredFragment(1) as NewsFragment).clearNewsList()
    }

    private fun displayArticleList(articleList: ArrayList<Article>) {
        val act = context as MainActivity
        (act.mainAdapter!!.getRegisteredFragment(1) as NewsFragment).displayArticles(articleList)
    }

    private fun getFeed(newsSource : String){
        val parser = Parser()
        parser.execute(newsSource)

        parser.onFinish(object : Parser.OnTaskCompleted {

            override fun onTaskCompleted(list: ArrayList<Article>) {
                displayArticleList(list)
            }

            override fun onError() {
                Log.d("an error occurred", "an error occurred getting feeds from $newsSource")
            }
        })
    }
}