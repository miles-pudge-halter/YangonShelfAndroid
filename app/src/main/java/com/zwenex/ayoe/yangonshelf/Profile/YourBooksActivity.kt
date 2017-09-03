package com.zwenex.ayoe.yangonshelf.Profile

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.Adapters.MyRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.Fragments.BookDialogFragment
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.UserDetails

import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.ActivityYourBooksBinding

class YourBooksActivity : AppCompatActivity(),MyRecyclerViewAdapter.OnClickListener {
    override fun onClick(position: Int, view: View) {
        val book : Book? = adapter.getItemAtPosition(position)
        val bookDialog : DialogFragment = BookDialogFragment(adapter.getAllItems()!!,position,true)

        val args = Bundle()
        args.putParcelable("book",book)
        bookDialog.arguments = args
        bookDialog.show(supportFragmentManager,"fragment_book_dialog")

    }

    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var adapter: MyRecyclerViewAdapter
    lateinit var bookList:ArrayList<Book>
    lateinit var binding: ActivityYourBooksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_your_books)

        bookList = ArrayList<Book>()
        adapter = MyRecyclerViewAdapter(this,this.bookList,true)
        adapter.clickListener = this
        val uid = intent.getStringExtra("uid")
        if(uid != FirebaseAuth.getInstance().currentUser?.uid){
            supportActionBar?.title = "Owned Books"
        }
        prepareBooks(uid)
        val mLayoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        val recyclerView = binding.yourbooksRecycler
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter
        swipeRefresh = binding.yourbooksSwipe
        swipeRefresh.setOnRefreshListener {
            prepareBooks(uid)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    fun prepareBooks(uid:String){
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("books")
        mDatabase.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("yangonfail","database error"+p0.toString()) //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                bookList.clear()
                for(book in dataSnapshot?.children!!){
                    val bookx: Book = book.getValue(Book::class.java)
                    bookx.id = book.key
                    if(bookx.owner == uid){
                        val uDatabase = FirebaseDatabase.getInstance().getReference("users/"+bookx.owner)
                        uDatabase.addValueEventListener(object:ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                                Log.e("fetch fail at all", p0.toString())
                            }

                            override fun onDataChange(userSnap: DataSnapshot?) {
                                bookx.ownerObj = userSnap!!.getValue(UserDetails::class.java)
                                bookList.add(bookx)
                                adapter.notifyDataSetChanged()
                            }
                        })
                    }
                    adapter.notifyDataSetChanged()
                }
                swipeRefresh.isRefreshing=false

            }
        })
    }
}
