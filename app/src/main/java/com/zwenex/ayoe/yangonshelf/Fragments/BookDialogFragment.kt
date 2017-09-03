package com.zwenex.ayoe.yangonshelf.Fragments


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zwenex.ayoe.yangonshelf.Adapters.BooksStatePagerAdapter
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.R


/**
 * A simple [Fragment] subclass.
 */
class BookDialogFragment(bookList : List<Book>,position: Int,var editable:Boolean) : DialogFragment() {

    var book : Book?=null
    var bookList : List<Book>
    var position :Int = 0
    init {
        this.position = position
        this.bookList = bookList
    }

    override fun onStart() {
        super.onStart()
        //DO THIS SHIT ON CLICK
        //(activity as TradeActivity).selectedBooks

        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.requestFeature(STYLE_NO_TITLE)
        val view :View = inflater!!.inflate(R.layout.dialog_viewpager,container,false)
        val viewPager = view.findViewById(R.id.book_pager)as ViewPager
        val dottedTab = view.findViewById(R.id.dotted_tab)as TabLayout
//        val myPagerAdapter = BooksPagerAdapter(this.tradeList,context)
//        viewPager.requestingAdapter = myPagerAdapter
//        viewPager.currentItem = position
//        myPagerAdapter.notifyDataSetChanged()
        val adapter = BooksStatePagerAdapter(childFragmentManager,bookList,editable)
        viewPager.adapter = adapter
        dottedTab.setupWithViewPager(viewPager,true)
        viewPager.currentItem = position
        adapter.notifyDataSetChanged()
        return view
    }

}
