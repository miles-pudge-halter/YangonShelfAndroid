package com.zwenex.ayoe.yangonshelf.Fragments
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.zwenex.ayoe.yangonshelf.Adapters.AddBookPagerAdapter
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.FragmentAddBookDialogBinding

/**
 * A simple [Fragment] subclass.
 */
class AddBookDialogFragment : DialogFragment() {


    var bookCover : ImageView? = null
    var coverUri : Uri?=null
    var newBook : Book = Book()
    lateinit var binding : FragmentAddBookDialogBinding
    lateinit var viewPager : ViewPager
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_add_book_dialog,container,false)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.bind(view)
        viewPager = binding.addbookPager
        val adapter = AddBookPagerAdapter(childFragmentManager,2)
        viewPager.adapter = adapter
        viewPager.beginFakeDrag()
        return view
    }

}// Required empty public constructor


