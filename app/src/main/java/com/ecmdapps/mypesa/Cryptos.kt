package com.ecmdapps.mypesa

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.*
import com.jjoe64.graphview.series.DataPoint
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Cryptos (private val context: Context, private val callingClass: String?) {
    private var coinList = ArrayList<Coin>()
    private var rate : Rate? = null
    private val baseCurrencyApiUrl = "https://free.currencyconverterapi.com/api/v5/convert?q="
    private val allCryptoUrl = "http://coincap.io/front"
    private var cDB: DatabaseReference = FirebaseDatabase.getInstance().getReference("rates")

    private var requestQueue = Volley.newRequestQueue(context)
    private var usdCurrencyCode: String = "USD"
    private var localeCountryCode : String = "KE"
    private var localeCurrencyCode : String = Currency.getInstance(Locale("", localeCountryCode)).currencyCode
    private var exchangeCurrencyPair : String =  "${usdCurrencyCode}_$localeCurrencyCode"
    private var currencyApiUrl : String = "$baseCurrencyApiUrl$exchangeCurrencyPair"

    private lateinit var currentCoin : Coin
    private lateinit var coinIDList : ArrayList<String>

    private fun getCurrencyApiUrl () : String {
        return currencyApiUrl
    }

    private fun makeRequest(url: String){
        if (url != getCurrencyApiUrl()){
            val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener<JSONArray> { response ->
                coinList = processCoins(response)
                displayCoinList(coinList)
            }, Response.ErrorListener { error -> Log.d("Volley Error", error.toString()) })
            requestQueue.add(jsonArrayRequest)
        } else {
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null, Response.Listener { response ->
                setKESVal(response)
            }, Response.ErrorListener { error -> Log.d("Volley Error", error.toString()) })
            requestQueue.add(jsonObjectRequest)
        }
    }


    private fun getUSDKES() {
        val currencyListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                rate = dataSnapshot.child(exchangeCurrencyPair).getValue(Rate::class.java)
                if (rate == null || ((rate!!.lastChanged - (System.currentTimeMillis()/1000)) > (30 * 60))){
                    makeRequest(currencyApiUrl)
                } else {
                    return
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("currency getter error", "loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        cDB.addValueEventListener(currencyListener)
    }

    private fun setKESVal(response: JSONObject?) {
        val cur : JSONObject? = response?.getJSONObject("results")?.getJSONObject(exchangeCurrencyPair)
        val ku : Any? = cur?.get("val")
        val to = cur?.getString("to")
        val from = cur?.getString("fr")
        val lastChanged = System.currentTimeMillis() /1000L

        val exchange = when (ku) {
            is Double -> ku
            is Int -> ku.toDouble()
            else -> 1.000
        }
        rate = Rate(exchange, to!!, from!!, lastChanged)
        cDB.child(exchangeCurrencyPair).setValue(rate)
        cDB.child(exchangeCurrencyPair).child("lastChanged").setValue((System.currentTimeMillis() / 1000L))
        cDB.child(exchangeCurrencyPair).child("rate").setValue(rate!!.rate)
    }

    private fun processCoins(response: JSONArray): ArrayList<Coin> {
        val coinList = ArrayList<Coin> ()
        (0 until response.length())
                .map { response.getJSONObject(it) }
                .mapTo(coinList) { getCoin(it) }
        return coinList
    }

    private fun getCoin(o: JSONObject) : Coin {
        val oPrice : Double
        val oMktCap : Double
        val oPerc : Double

        oPrice = when {
            o.get("price") is Int -> {
                val price = o.get("price") as Int
                price.toDouble()
            }
            else -> o.get("price") as Double
        }

        oPerc = when {
            o.get("perc") is Int -> {
                val perc = o.get("perc") as Int
                perc.toDouble()
            }
            else -> o.get("perc") as Double
        }

        oMktCap = when {
            o.get("mktcap") is Int -> {
                val mktcap = o.get("mktcap") as Int
                mktcap.toDouble()
            }
            o.get("mktcap") is Long -> {
                val mktcap = o.get("mktcap") as Long
                mktcap.toDouble()
            }
            else -> o.get("mktcap") as Double
        }

        return Coin(o["long"] as String, o["short"] as String, (oPrice * rate!!.rate), oPrice, ( oMktCap * rate!!.rate), oMktCap, oPerc)
    }

    private fun displayCoinList(coinList: ArrayList<Coin>) {
        when (callingClass) {
            context.getString(R.string.MainActivityClassName) -> {
                val act = context as MainActivity
                act.contentProgressBar.visibility = View.GONE
                act.displayCoins(coinList)
            }
            context.getString(R.string.AvailableCryptosActivityClassName) -> {
                val act = context as AvailableCryptosActivity
                act.contentProgressBar.visibility = View.GONE
                act.displayCoins(coinList)
            }
        }
    }


    private fun displayPriceGraph(priceList: Array<DataPoint>) {
        (context as CoinHistoryActivity).displayCoinGraph(priceList)
    }

    fun loadCoins(cIDList: ArrayList<String>){
        showProgressBar()
        coinIDList = cIDList
        if (rate == null || (rate!!.lastChanged - (System.currentTimeMillis()/1000)) >= (5 * 60)) {
            GetExchangeRate(this).execute(exchangeCurrencyPair)
        } else {
            val nullCIDList : Array<String?> = arrayOfNulls(coinIDList.size)
            @Suppress("UNCHECKED_CAST")
            val cList : Array<String> = coinIDList.toArray(nullCIDList) as Array<String>
            GetCoins(this).execute(*cList)
        }
    }

    fun loadAvailableCoins() {
        showProgressBar()
        if (rate == null || (rate!!.lastChanged - (System.currentTimeMillis()/1000)) >= (5 * 60)) {
            GetExchangeRate(this).execute(exchangeCurrencyPair)
        } else {
            makeRequest(allCryptoUrl)
        }
    }

    private fun showProgressBar() {
        when (callingClass) {
            context.getString(R.string.MainActivityClassName) -> {
                val act = context as MainActivity
                act.contentProgressBar.visibility = View.VISIBLE
            }
            context.getString(R.string.AvailableCryptosActivityClassName) -> {
                val act = context as AvailableCryptosActivity
                act.contentProgressBar.visibility = View.VISIBLE
            }
        }
    }


    fun loadCoinGraph(coin: Coin) {
        currentCoin = coin
        if (rate == null || ((rate!!.lastChanged - (System.currentTimeMillis()/1000)) > (5 * 60))){
            GetExchangeRate(this).execute(exchangeCurrencyPair)
        } else {
            GetHistoricalCoinData(this).execute(coin.id)
        }
    }

    companion object {
        class GetExchangeRate(private val cryptos: Cryptos) : AsyncTask<String, Int, Long>() {
            override fun doInBackground(vararg params: String?): Long {
                val count = params.size
                val totalSize = 0L

                for (i in 0 until count) {
                    cryptos.getUSDKES()
                    publishProgress((i / count.toFloat() * 100).toInt())
                    if (isCancelled) break
                }
                return totalSize
            }

            override fun onPostExecute(result: Long?) {
                super.onPostExecute(result)
                when (cryptos.callingClass) {
                    cryptos.context.getString(R.string.MainActivityClassName) -> cryptos.loadCoins(cryptos.coinIDList)
                    cryptos.context.getString(R.string.AvailableCryptosActivityClassName) -> cryptos.loadAvailableCoins()
                    cryptos.context.getString(R.string.CoinHistoryActivityClassName) -> cryptos.loadCoinGraph(cryptos.currentCoin)
                }
            }
        }

        class GetHistoricalCoinData(private val cryptos: Cryptos) : AsyncTask<String, Int, Long>() {
            private lateinit var priceList: Array<DataPoint>
            private var requestQueue = Volley.newRequestQueue(cryptos.context)
            override fun doInBackground(vararg params: String?): Long {
                val count = params.size
                val totalSize = 0L

                for (i in 0 until count) {
                    val url = "http://coincap.io/history/${params[i]}"
                    val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener<JSONObject> { response ->
                        priceList = processHistory(response.get("price") as JSONArray)
                        cryptos.displayPriceGraph(priceList)
                    }, Response.ErrorListener { error -> Log.d("Volley Error", error.toString()) })
                    requestQueue.add(jsonObjectRequest)
                    publishProgress((i / count.toFloat() * 100).toInt())
                    if (isCancelled) break
                }
                return totalSize

            }

            private fun processHistory(jsonArray: JSONArray?): Array<DataPoint> {
                val points = arrayOfNulls<DataPoint>(jsonArray!!.length())

                for (i in 0 until jsonArray.length()) {
                    val historyArray : JSONArray = jsonArray[i] as JSONArray
                    val histDate = Date(historyArray[0] as Long)
                    val histPrice = when {
                        historyArray[1] is Int -> {
                            val price = historyArray[1] as Int
                            price.toDouble()
                        }
                        historyArray[1] is Long -> {
                            val price = historyArray[1] as Long
                            price.toDouble()
                        }
                        else -> historyArray[1] as Double
                    }
                    points[i] = DataPoint(histDate, (histPrice * cryptos.rate!!.rate))
                }

                @Suppress("UNCHECKED_CAST")
                return points as Array<DataPoint>
            }
        }

        class GetCoins(private  val cryptos: Cryptos): AsyncTask<String, Int, Long>() {
            private val coinList = ArrayList<Coin> ()
            private var requestQueue = Volley.newRequestQueue(cryptos.context)

            override fun doInBackground(vararg params: String): Long {
                val count = params.size
                val totalSize = 0L

                val url = "http://coincap.io/front"

                val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener<JSONArray> { response ->
                    for (i in 0 until count) {
                        val coin = findCoin(params[i], response)
                        coinList.add(coin)
                        publishProgress((i / count.toFloat() * 100).toInt())
                        if (isCancelled) break
                    }
                    cryptos.displayCoinList(coinList)
                }, Response.ErrorListener { error -> Log.d("Volley Error", error.toString()) })
                requestQueue.add(jsonArrayRequest)
                return totalSize
            }

            private fun findCoin(coinID: String, response: JSONArray?): Coin {
                lateinit var coin : Coin
                for (i in 0 until response?.length()!!){
                    val myCoin = response.getJSONObject(i)
                    if (myCoin["short"] == coinID){
                        coin = cryptos.getCoin(myCoin)
                        break
                    }
                }
                return coin
            }
        }
    }
}