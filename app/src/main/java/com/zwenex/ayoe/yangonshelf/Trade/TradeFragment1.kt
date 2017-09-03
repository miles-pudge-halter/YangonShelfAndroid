package com.zwenex.ayoe.yangonshelf.Trade

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.ActivityTradeBinding
import com.zwenex.ayoe.yangonshelf.databinding.FragmentTradeFragment1Binding
import com.zwenex.ayoe.yangonshelf.databinding.TradeBookCardBinding
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.rabbitconverter.rabbit.Rabbit

class TradeFragment1 : Fragment(), TradeRecyclerViewAdapter.OnClickListener {
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
    lateinit var binding : FragmentTradeFragment1Binding
    lateinit var adapter : TradeRecyclerViewAdapter
    lateinit var bookList : ArrayList<Book>
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater!!.inflate(R.layout.fragment_trade_fragment1,container,false)
        binding = DataBindingUtil.bind(myView)
        bookList = ArrayList<Book>()
        adapter = TradeRecyclerViewAdapter(context, bookList ,"frag1",this)
        adapter.removeSelection()
        adapter.clickListener = this
        val myLayoutManager : RecyclerView.LayoutManager = GridLayoutManager(context,2)
        val myRecyclerView = binding.tradeRecyclerView
        myRecyclerView.layoutManager = myLayoutManager
        myRecyclerView.adapter = adapter
        fetchUser()
        prepareBooks(FirebaseAuth.getInstance().currentUser!!.uid)
        binding.tradeNextBtn.setOnClickListener {
            (activity as TradeActivity).selectedBooks1 = adapter.selectedItems
            (activity as TradeActivity).pager.setCurrentItem(1,true)
        }

        return myView
    }
    fun prepareBooks(uid:String){
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("books")
        mDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("yangonfail", "database error" + p0.toString()) //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    val book:Book = data.getValue(Book::class.java)
                    book.id=data.key
                    if(book.owner == uid && book.currentHolder.isNullOrBlank() && !book.pending)
                        bookList.add(book)
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
    fun fetchUser(){
        val mDatabase = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().currentUser?.uid)
        mDatabase.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(data: DataSnapshot) {
                val trader = data.getValue(UserDetails::class.java)
                if(Pref.fontChoice=="zawgyi")
                    binding.trader1Name.text = Rabbit.uni2zg(trader?.displayName)
                else
                    binding.trader1Name.text = trader?.displayName
                Glide.with(context)
                        .load("http://graph.facebook.com/"+trader?.fbID+"/picture")
                        .bitmapTransform(CropCircleTransformation(context))
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .crossFade().into(binding.trader1ProfilePic)
            }

        })
    }
}
