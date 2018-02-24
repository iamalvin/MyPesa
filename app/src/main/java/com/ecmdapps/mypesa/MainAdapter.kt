package com.ecmdapps.mypesa

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager


class MainAdapter (fm: FragmentManager) : SmartFragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return CoinFragment.newInstance()
            1 -> return NewsFragment.newInstance()
        }
        return CoinFragment.newInstance()
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}