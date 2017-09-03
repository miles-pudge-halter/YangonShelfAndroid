package com.zwenex.ayoe.yangonshelf.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.zwenex.ayoe.yangonshelf.Fragments.AddBookFragment1
import com.zwenex.ayoe.yangonshelf.Fragments.AddBookFragment2
import xyz.santeri.wvp.WrappingFragmentStatePagerAdapter

class AddBookPagerAdapter(fm: FragmentManager,internal var NumbOfTabs: Int
) : WrappingFragmentStatePagerAdapter(fm) {

    //This method return the fragment for the every position in the View Pager
    override fun getItem(position: Int): Fragment {
        if (position == 0)
            return  AddBookFragment1()
        else
            return AddBookFragment2()
    }
    override fun getCount(): Int {
        return NumbOfTabs
    }
}
