package com.zwenex.ayoe.yangonshelf.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zwenex.ayoe.yangonshelf.Fragments.BooksPagerFragment
import com.zwenex.ayoe.yangonshelf.Models.Book
import xyz.santeri.wvp.WrappingFragmentStatePagerAdapter

/**
 * Created by ayoe on 7/24/17.
 */
class BooksStatePagerAdapter(var fm: FragmentManager?,var bookList: List<Book>,var editable:Boolean) : WrappingFragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return BooksPagerFragment(bookList[position],editable)
    }

    override fun getCount(): Int {
        return bookList.size
    }

}