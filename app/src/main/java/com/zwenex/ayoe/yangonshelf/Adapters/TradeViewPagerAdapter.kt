package com.zwenex.ayoe.yangonshelf.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.zwenex.ayoe.yangonshelf.Fragments.IncomingTradesFragment
import com.zwenex.ayoe.yangonshelf.Fragments.OutgoingTradesFragment

class TradeViewPagerAdapter(fm: FragmentManager,internal var NumbOfTabs: Int
) : FragmentStatePagerAdapter(fm) {

    //This method return the fragment for the every position in the View Pager
    override fun getItem(position: Int): Fragment {
        if (position == 0)
            return  IncomingTradesFragment()
        else
            return OutgoingTradesFragment()
    }
    override fun getCount(): Int {
        return NumbOfTabs
    }
}
