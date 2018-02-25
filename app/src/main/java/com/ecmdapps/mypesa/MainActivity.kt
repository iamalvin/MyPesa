package com.ecmdapps.mypesa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val setCountry = sharedPref.getString(getString(R.string.countryPreference), "")
        if (setCountry == ""){
            showCurrencySelector()
        }
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
            R.id.action_change_currency -> { showCurrencySelector(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun viewAllCrypto()  {
        val intent = Intent(this, AvailableCryptosActivity::class.java)
        startActivity(intent)
    }

    private fun rebuild() {
        finish()
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    @SuppressLint("ApplySharedPref")
    private fun showCurrencySelector(){
        val locales : Array<Locale> = Locale.getAvailableLocales()
        val currencies = ArrayList<String>()
        val currencyNames = ArrayList<String>()
        val currencyMap = HashMap<String, String>()

        for (i in 0 until locales.size){
            try {
                val locale = locales[i]
                val currency = Currency.getInstance(locale).currencyCode
                val country = locale.displayCountry
                currencies.add("$currency - $country")
                currencyNames.add(currency)
                currencyMap[currency] = locale.country
            } catch(e: Exception) {
                continue
            }
        }

        Log.d("currency map", currencyMap.toString())
        @Suppress("UNCHECKED_CAST")
        val currencyArray =  arrayOfNulls<String>(currencies.size)
        for (i in 0 until currencies.size){
            currencyArray[i] = currencies[i]
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Currency")

        builder.setItems(currencyArray, { dialog, which ->
            val selectedCountry = currencyMap[currencyNames[which]]
            Log.d("selected country", selectedCountry)
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(getString(R.string.countryPreference), selectedCountry)
            editor.commit()
            dialog.dismiss()
            rebuild()
        })
        builder.show()
    }
}
