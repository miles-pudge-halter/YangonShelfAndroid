package com.zwenex.ayoe.yangonshelf

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import com.google.firebase.database.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.zwenex.ayoe.yangonshelf.Adapters.MyRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.Adapters.MySearchSuggestionBuilder
import com.zwenex.ayoe.yangonshelf.Fragments.BookDialogFragment
import com.zwenex.ayoe.yangonshelf.Fragments.BookSheetFragment
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.databinding.ActivitySearchBinding
import org.cryse.widget.persistentsearch.PersistentSearchView


fun <T:View> Activity.findViewId(@IntegerRes id:Int):T{
    return this.findViewById(id) as T
}
class SearchActivity : AppCompatActivity(), MyRecyclerViewAdapter.OnClickListener {

    override fun onClick(position: Int, view: View) {
//        val book : Book? = requestingAdapter!!.getItemAtPosition(position)
//        var bookDialog : DialogFragment = BookDialogFragment(requestingAdapter!!.getAllItems()!!,position)
//
//        var args = Bundle()
//        args.putParcelable("book",book)
//        bookDialog.arguments = args
//        bookDialog.show(fragmentManager,"fragment_book_dialog")
        val btmSheetFragment = BookSheetFragment(adapter!!.getAllItems()!!,position)
        btmSheetFragment.show(supportFragmentManager,btmSheetFragment.tag)
    }

    lateinit var binding:ActivitySearchBinding
    var query: String?=null
    lateinit var searchView : PersistentSearchView
    var bookList:ArrayList<Book>?= null
    var adapter : MyRecyclerViewAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search)

        //val toolbar = findViewId<Toolbar>(R.id.search_toolbar)
        //toolbar.title = "Yangon Shelf"
        //setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bookList = ArrayList<Book>()
        query = intent.getStringExtra("query")

        supportActionBar?.title = "Searching: "+query

        adapter = MyRecyclerViewAdapter(this,this.bookList!!,false)
        adapter!!.clickListener = this
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = adapter

        Log.e("Query: ",query)
        searchView = binding.searchView2
        searchView.setSuggestionBuilder(MySearchSuggestionBuilder(this))
        searchView.setSearchString(query,true)
        searchView.openSearch(query)

        searchView.setHomeButtonListener {
            super.onBackPressed()
        }
        searchView.setSearchListener(object : PersistentSearchView.SearchListener{
            override fun onSearch(searchText: String?) {
                query = searchText
                searchBooks(query!!)
                //supportActionBar!!.title = "Searching: "+query
            }
            override fun onSearchExit() {
            }
            override fun onSearchCleared() {
            }
            override fun onSearchEditClosed() {
            }
            override fun onSearchTermChanged(term: String?) {
            }
            override fun onSearchEditBackPressed(): Boolean {
                searchView.closeSearch()
                return true
            }
            override fun onSearchEditOpened() {
            }
        })

        searchBooks(query!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.home,menu)
//        val item = menu.findItem(R.id.action_search)
//        searchView!!.setMenuItem(item)
//        return true
//    }

    override fun onBackPressed() {
        if(searchView.searchOpen) {
            searchView.closeSearch()
        }
        else{
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun searchBooks(query:String){
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("books")
        mDatabase.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("yangonfail","database error"+p0.toString()) //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                Log.e("book count", dataSnapshot?.childrenCount.toString())
                bookList!!.clear()
                for(book in dataSnapshot?.children!!) {

                    val bookx: Book = book.getValue(Book::class.java)
                    if (bookx.title!!.toLowerCase().contains(query.toLowerCase()) || bookx.author!!.toLowerCase().contains(query.toLowerCase()) || bookx.genres!!.contains(query)){
                        val uDatabase = FirebaseDatabase.getInstance().getReference("users/" + bookx.owner)
                        uDatabase.addValueEventListener(object : ValueEventListener {
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
                }
            }
        })
    }
}
