package com.ecmdapps.mypesa

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class CoinListAdapter(private val ctx: Context, private val coinList: ArrayList<Coin>) : BaseAdapter() {
    private val a : Activity = ctx as Activity
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val vh: ViewHolder
        val dbHandler = CoinsDatabaseHandler(ctx)

        if (convertView == null) {
            view = a.layoutInflater.inflate(R.layout.coin, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        val localeCountryCode = "KE"
        val format = NumberFormat.getCurrencyInstance(Locale("", localeCountryCode))
        val percFormat = NumberFormat.getPercentInstance(Locale("", localeCountryCode))

        format.maximumFractionDigits = 2
        percFormat.maximumFractionDigits = 2

        vh.cTitle.text = coinList[position].name
        vh.cPrice.text = format.format(coinList[position].value)
        vh.cId.text = coinList[position].id
        vh.dayChange.text = percFormat.format((coinList[position].percentChange/100))

        if (dbHandler.isCoinInDB(coinList[position].id)){
            vh.cFollowButton.text = ctx.getString(R.string.stopFollowing)
        } else {
            vh.cFollowButton.text = ctx.getString(R.string.startFollowing)
        }

        vh.cFollowButton.setOnClickListener {
            if (dbHandler.isCoinInDB(coinList[position].id)) {
                val selectionArgs = arrayOf(coinList[position].id)
                dbHandler.delete("${CoinsDatabaseHandler.colCoinId}=?", selectionArgs)
                vh.cFollowButton.text = ctx.getString(R.string.startFollowing)
            } else {
                val values = ContentValues()
                values.put(CoinsDatabaseHandler.colCoinName, coinList[position].name)
                values.put(CoinsDatabaseHandler.colCoinId, coinList[position].id)
                val insertID = dbHandler.add(values)
                if ( coinList[position].id == insertID ){
                    vh.cFollowButton.text = ctx.getString(R.string.stopFollowing)
                }
            }
        }

        when {
            coinList[position].percentChange > 0 -> vh.dayChange.setTextColor(Color.GREEN)
            coinList[position].percentChange < 0 -> vh.dayChange.setTextColor(Color.RED)
        }

        view!!.setOnClickListener({
            val intent = Intent(ctx, CoinHistoryActivity::class.java)
            intent.putExtra("coin", coinList[position])
            (ctx as Activity).startActivity(intent)
        })

        return view
    }

    override fun getItem(position: Int): Coin {
        return coinList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return coinList.size
    }

    class ViewHolder(view: View?){
        val cTitle: TextView = view?.findViewById(R.id.cTitle) as TextView
        val cId: TextView = view?.findViewById(R.id.cId) as TextView
        val cPrice: TextView = view?.findViewById(R.id.cPrice) as TextView
        val dayChange: TextView = view?.findViewById(R.id.dayChange) as TextView
        val cFollowButton : Button = view?.findViewById(R.id.cFollowButton) as Button
    }

}