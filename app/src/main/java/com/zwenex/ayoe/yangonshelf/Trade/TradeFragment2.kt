package com.zwenex.ayoe.yangonshelf.Trade

import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.FeedNavActivity
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.Notification
import com.zwenex.ayoe.yangonshelf.Models.Trade
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.FragmentTradeFragment2Binding
import com.zwenex.ayoe.yangonshelf.databinding.TradeBookCardBinding
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.rabbitconverter.rabbit.Rabbit
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TradeFragment2 : Fragment(), TradeRecyclerViewAdapter.OnClickListener {
    override fun onClick(position: Int, view: View) {
        val book = adapter.getItemAtPosition(position)
        adapter.toggleSelection(book.id!!)
        val binding : TradeBookCardBinding = DataBindingUtil.bind(view)
        val selected = binding.selected
        if(selected.visibility != ImageView.VISIBLE) {
            selected.visibility = ImageView.VISIBLE
            binding.cardView.cardElevation = 30f
        }
        else{
            selected.visibility = ImageView.INVISIBLE
            binding.cardView.cardElevation = 5f
        }
    }
    lateinit var binding : FragmentTradeFragment2Binding
    lateinit var adapter : TradeRecyclerViewAdapter
    lateinit var bookList : ArrayList<Book>
    lateinit var uid:String
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater!!.inflate(R.layout.fragment_trade_fragment2,container,false)
        binding = DataBindingUtil.bind(myView)
        bookList = ArrayList<Book>()
        adapter = TradeRecyclerViewAdapter(context, bookList,"frag2",this)
        adapter.clickListener = this
        val myLayoutManager : RecyclerView.LayoutManager = GridLayoutManager(context,2)
        val myRecyclerView = binding.tradeRecyclerView
        myRecyclerView.layoutManager = myLayoutManager
        myRecyclerView.adapter = adapter

        uid = (activity as TradeActivity).book.owner!!

        prepareBooks(uid)

        val trader = (activity as TradeActivity).book.ownerObj
        if(Pref.fontChoice=="zawgyi")
            binding.trader2Name.text = Rabbit.uni2zg(trader?.displayName)
        else
            binding.trader2Name.text = trader?.displayName
        Glide.with(context)
                .load("http://graph.facebook.com/"+trader?.fbID+"/picture")
                .bitmapTransform(CropCircleTransformation(context))
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .crossFade().into(binding.trader2ProfilePic)

        binding.tradeBackBtn.setOnClickListener {
            (activity as TradeActivity).pager.setCurrentItem(0,true)
        }

        binding.tradeOfferBtn.setOnClickListener {
            (activity as TradeActivity).selectedBooks2 = adapter.selectedItems
            val selectedBooks1 = (activity as TradeActivity).selectedBooks1
            val selectedBooks2 = (activity as TradeActivity).selectedBooks2

            Log.e("Side 1 books: ",selectedBooks1.toString())
            Log.e("Side 2 books: ",selectedBooks2.toString())
            if(selectedBooks1.isEmpty() || selectedBooks2.isEmpty())
                Snackbar.make((activity as TradeActivity)
                        .findViewById(R.id.co_od_layout), "You must select at least one book on both sides", 1500)
                        .show()
            else{
                beginTrade(selectedBooks1, selectedBooks2)
            }
        }

        return myView
    }
    fun prepareBooks(uid:String){
        var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("books")
        mDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("yangonfail", "database error" + p0.toString()) //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    val book: Book = data.getValue(Book::class.java)
                    book.id=data.key
                    if(book.owner == uid && book.currentHolder.isNullOrBlank() && !book.pending)
                        bookList.add(book)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
    fun beginTrade(giving : ArrayList<String>, requesting:ArrayList<String>){
        val pd=ProgressDialog(activity)
        pd.setMessage("Sending trade offer")
        pd.show()
        val trade = Trade()
        //Mon Aug 21 2017 14:32:06 GMT+0630 (Myanmar Standard Time)
        val simpleDateFormat = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss (zzzz)")
        trade.giving = giving
        trade.requesting = requesting
        trade.requestee = uid
        trade.requester = FirebaseAuth.getInstance().currentUser!!.uid

        trade.timestamp = simpleDateFormat.format(Date())

        val tradeRef = FirebaseDatabase.getInstance().reference.child("trade")
        val trade_key = tradeRef.push().key
        val tradeUpdate = HashMap<String,Any>()
        tradeUpdate.put(trade_key,trade.toHashMap())
        tradeRef.updateChildren(tradeUpdate)


        val notiRef = FirebaseDatabase.getInstance().reference.child("noti").child(uid)
        val notiKey = notiRef.push().key
        val noti = Notification()

        val timestamp = simpleDateFormat.format(Date())
        Log.e("timestamp:",timestamp.toString())
        noti.requester = trade.requester
        noti.seen = false
        noti.tradeID = trade_key
        noti.type = "request"
        noti.timestamp = timestamp
        val notiUpdate = HashMap<String,Any>()
        notiUpdate.put(notiKey,noti.toHashMap())
        notiRef.updateChildren(notiUpdate).addOnCompleteListener {
            pd.hide()
            val intent = Intent(activity, FeedNavActivity::class.java)
            startActivity(intent)
            (activity as TradeActivity).finish()
        }

    }
}