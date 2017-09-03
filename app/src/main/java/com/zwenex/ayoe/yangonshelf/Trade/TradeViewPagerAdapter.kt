package com.zwenex.ayoe.yangonshelf.Trade

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by ayoe on 7/27/17.
 */

class TradeViewPagerAdapter (fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if(position == 0) return TradeFragment1()
        else return TradeFragment2()
    }

    override fun getCount(): Int {
        return 2
    }


}

