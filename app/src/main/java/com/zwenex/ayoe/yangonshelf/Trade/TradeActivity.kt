package com.zwenex.ayoe.yangonshelf.Trade

import android.databinding.DataBindingUtil
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.ActivityTradeBinding

class TradeActivity : AppCompatActivity() {

    var selectedBooks1 : ArrayList<String> = ArrayList()
    var selectedBooks2 : ArrayList<String> = ArrayList()
    lateinit var book:Book
    lateinit var binding : ActivityTradeBinding
    lateinit var pager : ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trade)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.elevation = 0f

        book = intent.extras.getParcelable<Book>("book")
        supportActionBar?.title = "Select books to trade"

        pager= binding.tradePager
        val adapter = TradeViewPagerAdapter(supportFragmentManager)
        pager.adapter = adapter

//        binding.footerText.setOnClickListener {
//            pager.setCurrentItem(1,true)
//        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
