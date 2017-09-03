package com.zwenex.ayoe.yangonshelf.Fragments

import android.app.Dialog
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.*
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zwenex.ayoe.yangonshelf.Adapters.BooksStatePagerAdapter
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.DialogViewpagerBinding
import com.zwenex.ayoe.yangonshelf.databinding.FragmentBookDialogBinding
import xyz.santeri.wvp.WrappingViewPager
import java.text.FieldPosition
import android.widget.FrameLayout


/**
 * Created by ayoe on 7/26/17.
 */
class BookSheetFragment(bookList:List<Book>,position: Int) : BottomSheetDialogFragment(){
    var bookList : List<Book>
    var position : Int
    init {
        this.position = position
        this.bookList = bookList
    }
    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
    lateinit var binding : DialogViewpagerBinding
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.dialog_viewpager,container,false)
        binding = DataBindingUtil.bind(v)
        
        val viewPager =binding.bookPager as ViewPager
        val dottedTab =binding.dottedTab as TabLayout
        dottedTab.setupWithViewPager(viewPager,true)
        val adapter = BooksStatePagerAdapter(childFragmentManager,bookList,false)
        viewPager.adapter = adapter
        viewPager.currentItem = position
        adapter.notifyDataSetChanged()

        return v
    }
}