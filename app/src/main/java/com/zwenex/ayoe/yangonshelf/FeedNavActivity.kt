package com.zwenex.ayoe.yangonshelf

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.zwenex.ayoe.yangonshelf.Fragments.NotificationFragment
import com.zwenex.ayoe.yangonshelf.Fragments.FeedFragment
import com.zwenex.ayoe.yangonshelf.Fragments.TradeFragment
import com.zwenex.ayoe.yangonshelf.Profile.ProfileFragment
import com.zwenex.ayoe.yangonshelf.databinding.ActivityFeedNavFragmentBinding
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView


class FeedNavActivity : AppCompatActivity() {

    var fragmentPoz = 0
    lateinit var binding : ActivityFeedNavFragmentBinding
    lateinit var fm : FragmentManager
    lateinit var navigation : BottomNavigationViewEx
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                fragmentPoz = 0
                if(fm.findFragmentByTag("books") == null)
                    fm.beginTransaction().replace(R.id.content, FeedFragment(),"books").commit()
            }
            R.id.navigation_notifications -> {
                fragmentPoz = 2
                if(fm.findFragmentByTag("noti") == null)
                    fm.beginTransaction().replace(R.id.content, NotificationFragment(),"noti").commit()
            }
            R.id.navigation_profile -> {
                fragmentPoz = 3
                if(fm.findFragmentByTag("profile") == null)
                    fm.beginTransaction().replace(R.id.content,ProfileFragment(),"profile").commit()
            }
            R.id.navigation_trade -> {
                fragmentPoz = 1
                if(fm.findFragmentByTag("trade") == null)
                    fm.beginTransaction().replace(R.id.content, TradeFragment(),"trade").commit()
            }
        }
        true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("state","nga yae state")
        outState?.putInt("fragState",fragmentPoz)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        //var v = inflater!!.inflate(R.layout.activity_feed_nav_fragment,container,false)
        super.onCreate(savedInstanceState)
        Log.e("FONT CHOICE:",Pref.fontChoice)
        fm = supportFragmentManager
        if(savedInstanceState!=null){
            Log.e("Current fragment - ",savedInstanceState.getInt("fragState").toString())
            fragmentPoz = savedInstanceState.getInt("fragState")
            when(fragmentPoz){
                0 -> if(fm.findFragmentByTag("books") == null)
                    fm.beginTransaction().replace(R.id.content, FeedFragment(),"books").commit()
                1 -> if(fm.findFragmentByTag("trade") == null)
                    fm.beginTransaction().replace(R.id.content, TradeFragment(),"trade").commit()
                2 -> if(fm.findFragmentByTag("noti") == null)
                    fm.beginTransaction().replace(R.id.content, NotificationFragment(),"noti").commit()
                3 -> if(fm.findFragmentByTag("profile") == null)
                    fm.beginTransaction().replace(R.id.content,ProfileFragment(),"profile").commit()
            }
        }
        else {
            fragmentPoz = 0
            fm.beginTransaction().replace(R.id.content, FeedFragment(),"books").commit()
        }
        binding = DataBindingUtil.setContentView(this,R.layout.activity_feed_nav_fragment)
        navigation = binding.navigation
        navigation.enableAnimation(true)
        navigation.enableItemShiftingMode(false)
        navigation.enableShiftingMode(false)
        //navigation.setTextVisibility(false)
        navigation.onNavigationItemSelectedListener = mOnNavigationItemSelectedListener

        //addBadgeAt(2,5)
    }

    private fun addBadgeAt(position: Int, number: Int): Badge {
        return QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(55f,0f,true)
                .bindTarget(binding.navigation.getBottomNavigationItemView(position))
    }

}
