package com.ecmdapps.mypesa

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.prof.rssparser.Article

class NewsAdapter(private val newsItems: ArrayList<Article>, private val context: Context) : RecyclerView.Adapter<NewsHolder>() {
    override fun getItemCount(): Int {
        return newsItems.toArray().count()
    }

    override fun onBindViewHolder(holder: NewsHolder?, position: Int) {
        holder!!.updateWithNewsItem(newsItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NewsHolder {
        val newsItem = LayoutInflater.from(parent!!.context).inflate(R.layout.item_news, parent, false) as LinearLayout
        return NewsHolder(newsItem, context)
    }
}