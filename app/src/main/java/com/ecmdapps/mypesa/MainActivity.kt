package com.ecmdapps.mypesa

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var mainAdapter : MainAdapter? = null
    private var mainViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        title = getString(R.string.main_activity_title)

        mainAdapter = MainAdapter(supportFragmentManager)
        mainViewPager = findViewById(R.id.container)
        mainViewPager!!.adapter = mainAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(mainViewPager)

        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_account_balance_wallet_white_48dp)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_rss_feed_white_48dp)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_all_currencies -> { viewAllCrypto(); true }
            R.id.action_refresh_coins -> { (mainAdapter!!.getRegisteredFragment(0) as CoinFragment).reload(); true}
            R.id.action_refresh_news -> { (mainAdapter!!.getRegisteredFragment(1) as NewsFragment).reload(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun viewAllCrypto()  {
        val intent = Intent(this, AvailableCryptosActivity::class.java)
        startActivity(intent)
    }
}
