package com.zwenex.ayoe.yangonshelf.Fragments

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.FragmentAddbook1Binding

/**
 * Created by aYoe on 8/15/2017.
 */
class AddBookFragment1 : Fragment(){
    val IMAGE_INTENT = 1996

    lateinit var bookTitle : TextView
    lateinit var bookAuthor : TextView
    lateinit var bookDesc : TextView
    lateinit var binding : FragmentAddbook1Binding
    lateinit var bookCover : ImageView
    var myBitmap : Bitmap?=null
    var coverUri : Uri? = null
    lateinit var newBook : Book
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater!!.inflate(R.layout.fragment_addbook_1, container, false)
        binding = DataBindingUtil.bind(view)

        bookTitle = binding.inputTitle
        bookAuthor = binding.inputAuthor
        bookDesc = binding.inputDesc
        bookCover = binding.inputCover

        bookCover.setOnClickListener {
            val intent : Intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_INTENT)
        }
        binding.nextBtn.setOnClickListener{
            if(!isValidTitle(bookTitle.text.toString())){
                binding.inputTitleLayout.error = "Invalid book title"
            }
            else if(!isValidAuthor(bookAuthor.text.toString())){
                binding.inputAuthorLayout.error = "Invalid book author"
            }
            else {
                newBook = Book()
                newBook.title = bookTitle.text.toString()
                newBook.author = bookAuthor.text.toString()
                newBook.description = bookDesc.text.toString()
                Log.e("Before passing in ",newBook.toString())
                Pref.addingBook = newBook
                Pref.addingCover = myBitmap
                //(parentFragment as AddBookDialogFragment).newBook = newBook
                //(parentFragment as AddBookDialogFragment).coverUri = coverUri
                (parentFragment as AddBookDialogFragment).viewPager.setCurrentItem(1, true)
            }
        }

        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_INTENT && resultCode == Activity.RESULT_OK){
            coverUri = data!!.data
            myBitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver,coverUri)
            binding.inputCover.setImageBitmap(myBitmap)
            //Glide.with(context).load(coverUri).into(bookCover)
            Log.e("Before passing  uri",coverUri.toString())
        }
    }
    fun isValidTitle(title:String):Boolean{
        return title.length>2
    }
    fun isValidAuthor(author:String):Boolean{
        return author.length>3
    }
}