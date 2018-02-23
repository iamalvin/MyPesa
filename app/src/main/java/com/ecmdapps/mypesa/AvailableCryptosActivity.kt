package com.ecmdapps.mypesa

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_available_cryptos.*
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
        refresh.setOnRefreshListener { cryptos.loadAvailableCoins(); refresh.isRefreshing = false }
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
        contentProgressBar.visibility = View.VISIBLE
        cryptos.loadAvailableCoins()
    }

    fun displayCoins(coinList: ArrayList<Coin>) {
        contentProgressBar.visibility = View.GONE
        val coinListAdapter = CoinListAdapter(this, coinList)
        lvCoins.adapter = coinListAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_available_cryptos, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_refresh -> { cryptos.loadAvailableCoins(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }
}
