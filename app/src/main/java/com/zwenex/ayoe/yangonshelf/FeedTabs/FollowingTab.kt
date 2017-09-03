package com.zwenex.ayoe.yangonshelf.FeedTabs


import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.Adapters.MyRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.Fragments.BookDialogFragment
import com.zwenex.ayoe.yangonshelf.Fragments.BookSheetFragment
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.Profile.ProfileEditSheet

import com.zwenex.ayoe.yangonshelf.R


/**
 * A simple [Fragment] subclass.
 */
class FollowingTab : Fragment() , MyRecyclerViewAdapter.OnClickListener {

    var swipeRefresh: SwipeRefreshLayout?=null
    var adapter:MyRecyclerViewAdapter?= null
    companion object {
        var fragment = FollowingTab()
    }
    var bookList:ArrayList<Book>?=null

    override fun onClick(position: Int, view: View) {
        val book : Book? = adapter!!.getItemAtPosition(position)
//        var bookDialog : DialogFragment = BookDialogFragment(requestingAdapter!!.getAllItems()!!,position)
//
//        var args = Bundle()
//        args.putParcelable("book",book)
//        bookDialog.arguments = args
//        bookDialog.show(fragmentManager,"fragment_book_dialog")
        var btmSheetFragment = BookSheetFragment(adapter!!.getAllItems()!!,position)
        btmSheetFragment.show(fragmentManager,btmSheetFragment.tag)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        bookList = ArrayList<Book>()
        adapter = MyRecyclerViewAdapter(context, this.bookList!!,false)

        adapter!!.clickListener = this

        prepareUser()
        //prepareBooks()
        val view = inflater!!.inflate(R.layout.fragment_following, container, false)
        val mLayoutManager : RecyclerView.LayoutManager = LinearLayoutManager(context)
        val recyclerView = view!!.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter

        swipeRefresh = view.findViewById(R.id.followingRefresh) as SwipeRefreshLayout
        swipeRefresh!!.setOnRefreshListener{
            prepareUser()
        }

        return view
    }
    fun prepareUser(){
        val uDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().currentUser!!.uid)
        uDatabase.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("User fetch fail", p0.toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                Log.e("Fetched User:", dataSnapshot.toString())
                val fetchedUser : UserDetails = dataSnapshot!!.getValue(UserDetails::class.java)
                Log.e("Fetched User2:",fetchedUser.toString())

                prepareBooks(fetchedUser.genres!!)
            }

        })
    }

    fun prepareBooks(genres:List<String>){
        val mDatabase = FirebaseDatabase.getInstance().getReference("books").orderByChild("title")

        mDatabase.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("yangonfail","database error"+p0.toString()) //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                Log.e("book count", dataSnapshot?.childrenCount.toString())
                bookList!!.clear()
                dataSnapshot?.children!!.forEach { book ->
                    val bookx:Book = book.getValue(Book::class.java)
                    bookx.id=book.key
                    bookx.genres!!.forEach{  gen->
                        if(genres.contains(gen) && !bookList!!.contains(bookx)){
                            val uDatabase = FirebaseDatabase.getInstance().getReference("users/"+bookx.owner)
                            uDatabase.addValueEventListener(object:ValueEventListener{
                                override fun onCancelled(p0: DatabaseError?) { Log.e("fetch fail at following",p0.toString()) }
                                override fun onDataChange(userSnap: DataSnapshot?) {
                                    bookx.ownerObj = userSnap!!.getValue(UserDetails::class.java)
                                    bookList!!.add(bookx)
                                    adapter!!.notifyDataSetChanged()
                                }
                            })
                        }
                    }
//                    bookx.id = book.key
//                    bookx.genres!!.forEach { genB ->
//                        genres.forEach { genU ->
//                            if(genB == genU)
//                                if(!bookList!!.contains(bookx)) {
//                                    val uDatabase = FirebaseDatabase.getInstance().getReference("users/"+bookx.owner)
//                                    uDatabase.addValueEventListener(object:ValueEventListener{
//                                        override fun onCancelled(p0: DatabaseError?) { Log.e("fetch fail at following",p0.toString()) }
//                                        override fun onDataChange(userSnap: DataSnapshot?) {
//                                            bookx.ownerObj = userSnap!!.getValue(UserDetails::class.java)
//                                            Log.e("FOUND:__",bookx.title)
//                                            bookList!!.add(bookx)
//                                            adapter!!.notifyDataSetChanged()
//                                        }
//                                    })
//                                }
//                        }
//                    }
                    //tradeList!!.add(bookx)
                }
                swipeRefresh!!.isRefreshing=false

            }
        })
    }
}
