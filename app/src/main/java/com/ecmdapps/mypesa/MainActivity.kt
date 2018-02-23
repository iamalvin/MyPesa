package com.ecmdapps.mypesa

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var cryptos: Cryptos
    private var coinIdList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        title = getString(R.string.main_activity_title)
        cryptos = Cryptos(this, getString(R.string.MainActivityClassName))

        loadMyCoins()
        fab.setOnClickListener { viewAllCrypto() }
    }


    private fun loadMyCoins() {
        val dbHandler = CoinsDatabaseHandler(this)
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

        cryptos.loadCoins(coinIdList)
    }

    private fun addEntry(dbHandler: CoinsDatabaseHandler, coinName: String, coinId: String) {
        val values = ContentValues()
        values.put(CoinsDatabaseHandler.colCoinName, coinName)
        values.put(CoinsDatabaseHandler.colCoinId, coinId)
        dbHandler.insert(values)
    }


    fun displayCoins(coinList: ArrayList<Coin>) {
        val coinListAdapter = CoinListAdapter(this, coinList)
        lvCoins.adapter = coinListAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_all_currencies -> { viewAllCrypto(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun viewAllCrypto()  {
        val intent = Intent(this, AvailableCryptosActivity::class.java)
        startActivity(intent)
    }

    public override fun onResume() {
        super.onResume()
        loadMyCoins()
    }
}
