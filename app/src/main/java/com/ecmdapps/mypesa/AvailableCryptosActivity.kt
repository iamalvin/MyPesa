package com.ecmdapps.mypesa

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class AvailableCryptosActivity : AppCompatActivity() {
    private var coinList = ArrayList<Coin>()
    private lateinit var cryptos: Cryptos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_cryptos)
        cryptos = Cryptos(this, getString(R.string.AvailableCryptosActivityClassName))
        loadCoins(savedInstanceState)
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        bundle?.putParcelableArrayList("coinList", coinList)
        super.onSaveInstanceState(bundle)
    }

    private fun loadCoins(savedInstanceState: Bundle?) {
        if (savedInstanceState != null){
            val cl = savedInstanceState.getParcelableArrayList<Coin>("coinList")
            displayCoins(cl)
        }
        cryptos.loadAvailableCoins()
    }

    fun displayCoins(coinList: ArrayList<Coin>) {
        val coinListAdapter = CoinListAdapter(this, coinList)
        lvCoins.adapter = coinListAdapter
    }


}
