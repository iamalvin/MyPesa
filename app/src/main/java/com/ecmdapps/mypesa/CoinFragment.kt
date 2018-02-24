package com.ecmdapps.mypesa

import android.content.ContentValues
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import kotlinx.android.synthetic.main.content_coins.*
import kotlinx.android.synthetic.main.fragment_coins.*
import java.util.*

class CoinFragment : Fragment() {
    private lateinit var cryptoSource: CryptoSource
    private var coinIdList = ArrayList<String>()

    companion object {
        fun newInstance(): CoinFragment {
            return CoinFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_coins, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cryptoSource = CryptoSource(context!!, getString(R.string.MainActivityClassName))
        loadMyCoins()
        fab.setOnClickListener { (context as MainActivity).viewAllCrypto() }
        refresh.setOnRefreshListener { loadMyCoins(); refresh.isRefreshing = false }
    }

    override fun onResume() {
        super.onResume()
        loadMyCoins()
    }


    private fun loadMyCoins() {
        val dbHandler = CoinsDatabaseHandler(context)
        val cursor = dbHandler.queryAll()

        coinIdList.clear()
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val coinId = cursor.getString(cursor.getColumnIndex(CoinsDatabaseHandler.colCoinId))
                coinIdList.add(coinId)
            } while (cursor.moveToNext())
            cursor.close()
        } else {
            addEntry(dbHandler, "Bitcoin", "BTC")
            addEntry(dbHandler, "Ethereum", "ETH")
            addEntry(dbHandler, "Ripple", "XRP")
            loadMyCoins()
        }
        val cpb = (context as MainActivity).findViewById(R.id.contentProgressBar) as ProgressBar
        cpb.visibility = View.VISIBLE
        cryptoSource.loadCoins(coinIdList)
    }

    private fun addEntry(dbHandler: CoinsDatabaseHandler, coinName: String, coinId: String) {
        val values = ContentValues()
        values.put(CoinsDatabaseHandler.colCoinName, coinName)
        values.put(CoinsDatabaseHandler.colCoinId, coinId)
        dbHandler.insert(values)
    }

    fun reload() {
        loadMyCoins()
    }

    fun displayCoins(coinList: ArrayList<Coin>) {
        contentProgressBar.visibility = View.GONE
        val coinListAdapter = CoinListAdapter(context, coinList)

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                coinListAdapter.filter.filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            override fun afterTextChanged(arg0: Editable) {}
        })

        lvCoins.adapter = coinListAdapter
    }
}