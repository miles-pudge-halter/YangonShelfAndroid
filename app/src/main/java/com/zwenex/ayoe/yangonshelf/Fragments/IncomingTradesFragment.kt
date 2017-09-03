package com.zwenex.ayoe.yangonshelf.Fragments


import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zwenex.ayoe.yangonshelf.Adapters.TradeListRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.Trade
import com.zwenex.ayoe.yangonshelf.Models.UserDetails

import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.ViewTradeActivity
import com.zwenex.ayoe.yangonshelf.databinding.FragmentIncomingTradesBinding
import com.zwenex.ayoe.yangonshelf.databinding.FragmentOutgoingTradesBinding
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class IncomingTradesFragment : Fragment(),TradeListRecyclerViewAdapter.OnClickListener {

    override fun onClick(position: Int, view: View) {
        val intent = Intent(activity, ViewTradeActivity::class.java)
        intent.putExtra("trade",adapter.getItemAtPosition(position))
        startActivity(intent)
    }
    lateinit var binding : FragmentIncomingTradesBinding
    lateinit var adapter : TradeListRecyclerViewAdapter
    lateinit var tradeList : ArrayList<Trade>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater!!.inflate(R.layout.fragment_incoming_trades, container, false)
        binding = DataBindingUtil.bind(view)
        tradeList = ArrayList<Trade>()

        val recyclerView = binding.incomingTrades
        val mLayoutManager  = LinearLayoutManager(context)
        adapter = TradeListRecyclerViewAdapter(context,tradeList)
        adapter.clickListener = this
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter
        getTrades(FirebaseAuth.getInstance().currentUser!!.uid)

        binding.swipeRefresh.setOnRefreshListener {
            getTrades(FirebaseAuth.getInstance().currentUser!!.uid)
        }

        return view
    }
    fun getTrades(uid:String) {
        tradeList.clear()
        val tradeRef = FirebaseDatabase.getInstance().reference
                .child("trade").orderByChild("requestee").equalTo(uid)
        tradeRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot?) {
                if (p0 != null) {
                    for (data in p0.children) {
                        val trade = data.getValue(Trade::class.java)
                        trade.id=data.key
                        val userRef = FirebaseDatabase.getInstance().reference
                                .child("users").child(trade.requester)
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p1: DatabaseError?) {}
                            override fun onDataChange(p1: DataSnapshot) {
                                trade.requester_obj = p1.getValue(UserDetails::class.java)
                                trade.giving_obj= ArrayList<Book>()
                                trade.requesting!!
                                        .map {
                                            FirebaseDatabase.getInstance().reference
                                                    .child("books").child(it)
                                        }
                                        .forEach {
                                            it.addListenerForSingleValueEvent(object: ValueEventListener{
                                                override fun onCancelled(p0: DatabaseError?) {}
                                                override fun onDataChange(book: DataSnapshot) {
                                                    trade.giving_obj!!.add(book.getValue(Book::class.java))
                                                    adapter.notifyDataSetChanged()
                                                }
                                            })
                                        }
                                tradeList.add(trade)
                                tradeList.reverse()
                                adapter.notifyDataSetChanged()
                            }
                        })
                    }
                }
            }
        })
        binding.swipeRefresh.isRefreshing = false
    }
}
