package com.ecmdapps.mypesa

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_coin_history.*
import java.text.NumberFormat
import java.util.*

class CoinHistoryActivity : AppCompatActivity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_coin_history)

        val localeCountryCode = "KE"
        val format = NumberFormat.getCurrencyInstance(Locale("", localeCountryCode))

        val coin : Coin = intent.getParcelableExtra("coin")
        cProgress.visibility = View.VISIBLE
        cTotalCap.text = format.format(coin.mktCap)

        Cryptos(this, getString(R.string.CoinHistoryActivityClassName)).loadCoinGraph(coin)
        title = coin.name
    }

    fun displayCoinGraph(priceList: Array<DataPoint>) {
        val series = LineGraphSeries(priceList)

        cGraph.viewport.isXAxisBoundsManual = true
        cGraph.viewport.setMinX(priceList[0].x)
        cGraph.viewport.setMaxX(priceList[priceList.size - 1].x)
        cGraph.viewport.isScalable = true

        cGraph.addSeries(series)
        cGraph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
        cGraph.gridLabelRenderer.numHorizontalLabels = 4
        cGraph.gridLabelRenderer.isHumanRounding = false

        cProgress.visibility = View.GONE
        cGraph.visibility = View.VISIBLE
    }
}