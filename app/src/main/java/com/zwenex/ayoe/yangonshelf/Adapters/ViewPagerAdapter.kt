package com.zwenex.ayoe.yangonshelf.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.zwenex.ayoe.yangonshelf.FeedTabs.AllTab

import com.zwenex.ayoe.yangonshelf.FeedTabs.FollowingTab

class ViewPagerAdapter(fm: FragmentManager, internal var Titles: Array<CharSequence>
 , internal var NumbOfTabs: Int // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
) : FragmentStatePagerAdapter(fm) {

    //This method return the fragment for the every position in the View Pager
    override fun getItem(position: Int): Fragment {
        if (position == 0)
            return  FollowingTab()
        else
            return AllTab()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return Titles[position]
    }

    // This method return the Number of tabs for the tabs Strip

    override fun getCount(): Int {
        return NumbOfTabs
    }
}
