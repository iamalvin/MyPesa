package com.ecmdapps.mypesa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.prof.rssparser.Article
import org.jsoup.Jsoup
import java.net.URL


class NewsHolder (itemView : View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val itemTitle: TextView = itemView.findViewById(R.id.nTitle)
    private val itemDescription: TextView = itemView.findViewById(R.id.nDescription)
    private val itemSourceName: TextView = itemView.findViewById(R.id.nSourceName)

    fun updateWithNewsItem(article : Article){
        val sourceDetails = URL(article.link).host
        itemTitle.text = article.title
        itemDescription.text = Jsoup.parse(article.description).text()
        itemSourceName.text = sourceDetails

        itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.link))
            context.startActivity(intent)
        }
    }
}