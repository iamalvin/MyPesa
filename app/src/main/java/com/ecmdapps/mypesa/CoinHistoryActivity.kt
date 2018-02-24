package com.ecmdapps.mypesa

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_coin_history.*
import org.json.JSONArray
import java.text.NumberFormat
import java.util.*


class CoinHistoryActivity : AppCompatActivity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_coin_history)

        val cryptos = CryptoSource(this, getString(R.string.CoinHistoryActivityClassName))

        val localeCountryCode = "KE"
        val format = NumberFormat.getCurrencyInstance(Locale("", localeCountryCode))

        val coin : Coin = intent.getParcelableExtra("coin")
        cProgress.visibility = View.VISIBLE
        cTotalCap.text = format.format(coin.mktCap)

        cryptos.loadCoinGraph(coin)
        cryptos.loadDayCoinGraph(coin)
        title = coin.name
    }

    fun displayCoinGraph(priceList: Array<DataPoint>, priceListArray: JSONArray) {

        cProgress.visibility = View.GONE
        val series = LineGraphSeries(priceList)
        val first = priceListArray[0] as JSONArray
        val firstDate = Date(first[0] as Long)

        cGraphMax.viewport.isXAxisBoundsManual = true
        cGraphMax.viewport.setMinX(priceList[0].x)
        cGraphMax.viewport.setMaxX(priceList[priceList.size - 1].x)
        cGraphMax.viewport.isScalable = true

        cGraphMax.addSeries(series)
        cGraphMax.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
        cGraphMax.gridLabelRenderer.numHorizontalLabels = 4
        cGraphMax.gridLabelRenderer.isHumanRounding = false
        cGraphMaxTitle.visibility = View.VISIBLE
        cGraphMaxTitle.setOnClickListener {
            cGraphMax.visibility = View.VISIBLE
            cGraphYear.visibility = View.GONE
            cGraphQuarter.visibility = View.GONE
            cGraphMonth.visibility = View.GONE
            cGraphWeek.visibility = View.GONE
            cGraphDay.visibility = View.GONE

            cGraphMaxTitle.setTextColor(Color.BLUE)
            cGraphDayTitle.setTextColor(Color.BLACK)
            cGraphWeekTitle.setTextColor(Color.BLACK)
            cGraphMonthTitle.setTextColor(Color.BLACK)
            cGraphQuarterTitle.setTextColor(Color.BLACK)
            cGraphYearTitle.setTextColor(Color.BLACK)
        }

        val aYearAgo = removeDays(365)
        if (firstDate < aYearAgo){
            cGraphYear.viewport.isXAxisBoundsManual = true
            cGraphYear.viewport.setMinX(aYearAgo.time.toDouble())
            cGraphYear.viewport.setMaxX(priceList[priceList.size - 1].x)
            cGraphYear.viewport.isScrollable = true

            cGraphYear.addSeries(series)
            cGraphYear.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
            cGraphYear.gridLabelRenderer.numHorizontalLabels = 4
            cGraphYear.gridLabelRenderer.isHumanRounding = false
            cGraphYearTitle.visibility = View.VISIBLE
            cGraphYearTitle.setOnClickListener {
                cGraphMax.visibility = View.GONE
                cGraphYear.visibility = View.VISIBLE
                cGraphQuarter.visibility = View.GONE
                cGraphMonth.visibility = View.GONE
                cGraphWeek.visibility = View.GONE
                cGraphDay.visibility = View.GONE

                cGraphMaxTitle.setTextColor(Color.BLACK)
                cGraphDayTitle.setTextColor(Color.BLACK)
                cGraphWeekTitle.setTextColor(Color.BLACK)
                cGraphMonthTitle.setTextColor(Color.BLACK)
                cGraphQuarterTitle.setTextColor(Color.BLACK)
                cGraphYearTitle.setTextColor(Color.BLUE)
            }
        }

        val aQuarterAgo = removeDays(62)
        if (firstDate < aQuarterAgo){
            cGraphQuarter.viewport.isXAxisBoundsManual = true
            cGraphQuarter.viewport.setMinX(aQuarterAgo.time.toDouble())
            cGraphQuarter.viewport.setMaxX(priceList[priceList.size - 1].x)
            cGraphQuarter.viewport.isScrollable = true

            cGraphQuarter.addSeries(series)
            cGraphQuarter.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
            cGraphQuarter.gridLabelRenderer.numHorizontalLabels = 4
            cGraphQuarter.gridLabelRenderer.isHumanRounding = false
            cGraphQuarterTitle.visibility = View.VISIBLE
            cGraphQuarterTitle.setOnClickListener {
                cGraphMax.visibility = View.GONE
                cGraphYear.visibility = View.GONE
                cGraphQuarter.visibility = View.VISIBLE
                cGraphMonth.visibility = View.GONE
                cGraphWeek.visibility = View.GONE
                cGraphDay.visibility = View.GONE

                cGraphMaxTitle.setTextColor(Color.BLACK)
                cGraphDayTitle.setTextColor(Color.BLACK)
                cGraphWeekTitle.setTextColor(Color.BLACK)
                cGraphMonthTitle.setTextColor(Color.BLACK)
                cGraphQuarterTitle.setTextColor(Color.BLUE)
                cGraphYearTitle.setTextColor(Color.BLACK)
            }
        }

        val aMonthAgo = removeDays(30)
        if (firstDate < aMonthAgo){
            cGraphMonth.viewport.isXAxisBoundsManual = true
            cGraphMonth.viewport.setMinX(aMonthAgo.time.toDouble())
            cGraphMonth.viewport.setMaxX(priceList[priceList.size - 1].x)
            cGraphMonth.viewport.isScrollable = true

            cGraphMonth.addSeries(series)
            cGraphMonth.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
            cGraphMonth.gridLabelRenderer.numHorizontalLabels = 4
            cGraphMonth.gridLabelRenderer.isHumanRounding = false
            cGraphMonthTitle.visibility = View.VISIBLE
            cGraphMonthTitle.setOnClickListener {
                cGraphMax.visibility = View.GONE
                cGraphYear.visibility = View.GONE
                cGraphQuarter.visibility = View.GONE
                cGraphMonth.visibility = View.VISIBLE
                cGraphWeek.visibility = View.GONE
                cGraphDay.visibility = View.GONE

                cGraphMaxTitle.setTextColor(Color.BLACK)
                cGraphDayTitle.setTextColor(Color.BLACK)
                cGraphWeekTitle.setTextColor(Color.BLACK)
                cGraphMonthTitle.setTextColor(Color.BLUE)
                cGraphQuarterTitle.setTextColor(Color.BLACK)
                cGraphYearTitle.setTextColor(Color.BLACK)
            }
        }

        val aWeekAgo = removeDays(7)
        if (firstDate < aWeekAgo){
            cGraphWeek.viewport.isXAxisBoundsManual = true
            cGraphWeek.viewport.setMinX(aWeekAgo.time.toDouble())
            cGraphWeek.viewport.setMaxX(priceList[priceList.size - 1].x)
            cGraphWeek.viewport.isScrollable = true

            cGraphWeek.addSeries(series)
            cGraphWeek.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
            cGraphWeek.gridLabelRenderer.numHorizontalLabels = 4
            cGraphWeek.gridLabelRenderer.isHumanRounding = false
            cGraphWeekTitle.visibility = View.VISIBLE
            cGraphWeekTitle.setOnClickListener {
                cGraphMax.visibility = View.GONE
                cGraphYear.visibility = View.GONE
                cGraphQuarter.visibility = View.GONE
                cGraphMonth.visibility = View.GONE
                cGraphWeek.visibility = View.VISIBLE
                cGraphDay.visibility = View.GONE

                cGraphMaxTitle.setTextColor(Color.BLACK)
                cGraphDayTitle.setTextColor(Color.BLACK)
                cGraphWeekTitle.setTextColor(Color.BLUE)
                cGraphMonthTitle.setTextColor(Color.BLACK)
                cGraphQuarterTitle.setTextColor(Color.BLACK)
                cGraphYearTitle.setTextColor(Color.BLACK)
            }
        }
    }

    private fun removeDays(days: Int) :Date {
        val d : Date = Calendar.getInstance().time
        return Date(d.time - days.toLong() * 1000 * 60 * 60 * 24)
    }

    fun displayDayCoinGraph(priceList: Array<DataPoint>) {
        cProgress.visibility = View.GONE
        val series = LineGraphSeries(priceList)

        cGraphDay.viewport.isXAxisBoundsManual = true
        cGraphDay.viewport.setMinX(priceList[0].x)
        cGraphDay.viewport.setMaxX(priceList[priceList.size - 1].x)
        cGraphDay.viewport.isScalable = true

        cGraphDay.addSeries(series)
        cGraphDay.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this)
        cGraphDay.gridLabelRenderer.numHorizontalLabels = 4
        cGraphDay.gridLabelRenderer.isHumanRounding = false
        cGraphDayTitle.visibility = View.VISIBLE

        cGraphDayTitle.setOnClickListener {
            cGraphMax.visibility = View.GONE
            cGraphYear.visibility = View.GONE
            cGraphQuarter.visibility = View.GONE
            cGraphMonth.visibility = View.GONE
            cGraphWeek.visibility = View.GONE
            cGraphDay.visibility = View.VISIBLE

            cGraphMaxTitle.setTextColor(Color.BLACK)
            cGraphDayTitle.setTextColor(Color.BLUE)
            cGraphWeekTitle.setTextColor(Color.BLACK)
            cGraphMonthTitle.setTextColor(Color.BLACK)
            cGraphQuarterTitle.setTextColor(Color.BLACK)
            cGraphYearTitle.setTextColor(Color.BLACK)
        }

        cGraphDayTitle.setTextColor(Color.BLUE)
        cGraphDay.visibility = View.VISIBLE
    }
}
