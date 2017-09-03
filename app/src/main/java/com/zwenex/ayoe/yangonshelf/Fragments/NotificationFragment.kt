package com.zwenex.ayoe.yangonshelf.Fragments


import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.Adapters.NotiRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.Notification
import com.zwenex.ayoe.yangonshelf.Models.Trade
import com.zwenex.ayoe.yangonshelf.Models.UserDetails

import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.ViewTradeActivity
import com.zwenex.ayoe.yangonshelf.databinding.FragmentNotificationBinding


/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment(),NotiRecyclerViewAdapter.OnClickListener {
    override fun onClick(position: Int, view: View) {
        val intent = Intent(activity, ViewTradeActivity::class.java)
        intent.putExtra("trade",adapter!!.getItemAtPosition(position).tradeObj)
        startActivity(intent)
    }

    var adapter:NotiRecyclerViewAdapter?=null
    var notiList:ArrayList<Notification>?=null
    var swipeRefresh:SwipeRefreshLayout?=null
    lateinit var binding : FragmentNotificationBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        notiList = ArrayList<Notification>()
        adapter = NotiRecyclerViewAdapter(context,this.notiList!!)
        adapter!!.clickListener = this
        prepareNotifications()
        val view = inflater!!.inflate(R.layout.fragment_notification,container, false)
        binding = DataBindingUtil.bind(view)
        val mLayoutManager : RecyclerView.LayoutManager = LinearLayoutManager(context)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter

        val toolbar = binding.toolbar
        toolbar.title=""
        val toolbarTitle = binding.toolbarTitle
        toolbarTitle.textSize = 26f
        toolbarTitle.typeface = Typeface.createFromAsset(context.assets,"font/PoiretOne-Regular.ttf")
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        swipeRefresh = binding.notiRefresh
        swipeRefresh!!.setOnRefreshListener {
            prepareNotifications()
        }

        return view
    }

    fun prepareNotifications(){
        val dbInstance = FirebaseDatabase.getInstance()
        notiList?.clear()
        val mDatabase = dbInstance.getReference("noti/"+FirebaseAuth.getInstance().currentUser?.uid)
        mDatabase.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) { Log.e("Noti fail","db error"+p0.toString()) }
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (noti in dataSnapshot?.children!!){
                    val myNoti:Notification = noti.getValue(Notification::class.java)
                    val req1Database:DatabaseReference = dbInstance.getReference("users/"+myNoti.requester)
                    req1Database.addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {Log.e("Noti fail","db error"+p0.toString())}
                        override fun onDataChange(snap1: DataSnapshot?) {
                            myNoti.requesterObj = snap1?.getValue(UserDetails::class.java)
                            dbInstance.getReference("trade/"+myNoti.tradeID)
                                    .addListenerForSingleValueEvent(object: ValueEventListener{
                                        override fun onCancelled(p0: DatabaseError?) {}
                                        override fun onDataChange(tradeSnap: DataSnapshot) {
                                            myNoti.tradeObj = tradeSnap.getValue(Trade::class.java)
                                            myNoti.tradeObj?.id = tradeSnap.key
                                            Log.e("trade in noti: ",myNoti.tradeObj.toString())
                                            dbInstance.getReference("users/"+myNoti.tradeObj?.requester)
                                                    .addListenerForSingleValueEvent(object:ValueEventListener{
                                                        override fun onCancelled(p0: DatabaseError?) {}
                                                        override fun onDataChange(p0: DataSnapshot) {
                                                            myNoti.tradeObj?.requester_obj=p0.getValue(UserDetails::class.java)
                                                            adapter?.notifyDataSetChanged()
                                                        }
                                                    })
                                            dbInstance.getReference("users/"+myNoti.tradeObj?.requestee)
                                                    .addListenerForSingleValueEvent(object:ValueEventListener{
                                                        override fun onCancelled(p0: DatabaseError?) {}
                                                        override fun onDataChange(p0: DataSnapshot) {
                                                            myNoti.tradeObj?.requestee_obj=p0.getValue(UserDetails::class.java)
                                                            adapter?.notifyDataSetChanged()
                                                        }
                                                    })
                                            myNoti.tradeObj?.requesting_obj = ArrayList<Book>()
                                            myNoti.tradeObj?.requesting?.forEach { book->
                                                dbInstance.getReference("books/"+book)
                                                        .addListenerForSingleValueEvent(object:ValueEventListener{
                                                            override fun onCancelled(p0: DatabaseError?) {}
                                                            override fun onDataChange(myBook: DataSnapshot) {
                                                                myNoti.tradeObj?.requesting_obj?.add(myBook.getValue(Book::class.java))
                                                                adapter?.notifyDataSetChanged()
                                                            }
                                                        })
                                            }
                                            myNoti.tradeObj?.giving_obj = ArrayList<Book>()
                                            myNoti.tradeObj?.giving?.forEach { book->
                                                dbInstance.getReference("books/"+book)
                                                        .addListenerForSingleValueEvent(object:ValueEventListener{
                                                            override fun onCancelled(p0: DatabaseError?) {}
                                                            override fun onDataChange(myBook: DataSnapshot) {
                                                                myNoti.tradeObj?.giving_obj?.add(myBook.getValue(Book::class.java))
                                                                adapter?.notifyDataSetChanged()
                                                            }
                                                        })
                                            }
                                            notiList?.add(myNoti)
                                            adapter?.notifyDataSetChanged()
                                        }
                                    })
                        }
                    })
                }
                adapter?.notifyDataSetChanged()
                Log.e("ITEM COUNT_____",adapter?.itemCount.toString())
                swipeRefresh!!.isRefreshing = false
            }

        })
    }

}// Required empty public constructor
