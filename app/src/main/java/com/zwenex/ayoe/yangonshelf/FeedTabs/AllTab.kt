package com.zwenex.ayoe.yangonshelf.FeedTabs


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.Adapters.MyRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.Fragments.BookDialogFragment
import com.zwenex.ayoe.yangonshelf.Fragments.BookSheetFragment
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.UserDetails

import com.zwenex.ayoe.yangonshelf.R


/**
 * A simple [Fragment] subclass.
 */
class AllTab : Fragment(), MyRecyclerViewAdapter.OnClickListener {
    override fun onClick(position: Int, view: View) {
        //val book : Book? = requestingAdapter!!.getItemAtPosition(position)
//        var bookDialog : DialogFragment = BookDialogFragment(requestingAdapter!!.getAllItems()!!,position)
//
//        var args = Bundle()
//        args.putParcelable("book",book)
//        bookDialog.arguments = args
//        bookDialog.show(fragmentManager,"fragment_book_dialog")
        var btmSheetFragment = BookSheetFragment(adapter!!.getAllItems()!!,position)
        btmSheetFragment.show(fragmentManager,btmSheetFragment.tag)
    }
    var adapter: MyRecyclerViewAdapter?= null
    var bookList:ArrayList<Book>?=null
    var swipeRefresh: SwipeRefreshLayout?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        bookList = ArrayList<Book>()
        adapter = MyRecyclerViewAdapter(context, this.bookList!!,false)

        adapter!!.clickListener = this

        prepareBooks()
        val view = inflater!!.inflate(R.layout.fragment_all, container, false)
        val mLayoutManager : RecyclerView.LayoutManager = LinearLayoutManager(context)
        val recyclerView = view!!.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter

        swipeRefresh = view.findViewById(R.id.allRefresh) as SwipeRefreshLayout
        swipeRefresh!!.setOnRefreshListener {
            prepareBooks()
        }

        return view
    }
    fun prepareBooks(){
        var mDatabase = FirebaseDatabase.getInstance().getReference("books").orderByChild("title")
        mDatabase.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("yangonfail","database error"+p0.toString()) //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                Log.e("book count", dataSnapshot?.childrenCount.toString())
                bookList!!.clear()
                for(book in dataSnapshot?.children!!){
                    var bookx:Book = book.getValue(Book::class.java)
                    bookx.id = book.key
                    val uDatabase = FirebaseDatabase.getInstance().getReference("users/"+bookx.owner)
                    uDatabase.addValueEventListener(object:ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            Log.e("fetch fail at all", p0.toString())
                        }

                        override fun onDataChange(userSnap: DataSnapshot?) {
                            bookx.ownerObj = userSnap!!.getValue(UserDetails::class.java)
                            bookList!!.add(bookx)
                            adapter!!.notifyDataSetChanged()
                        }
                    })
                }
                swipeRefresh!!.isRefreshing = false
            }
        })
    }
}
