package com.zwenex.ayoe.yangonshelf.Fragments

import android.app.ProgressDialog
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
import android.widget.Toast
import com.adroitandroid.chipcloud.ChipListener
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.FragmentAddbook2Binding
import org.rabbitconverter.rabbit.Rabbit
import java.io.ByteArrayOutputStream

/**
 * Created by aYoe on 8/15/2017.
 */
class AddBookFragment2 : Fragment(){
    lateinit var binding : FragmentAddbook2Binding
    lateinit var genres: Array<String>
    lateinit var selectedGenres: ArrayList<String>
    lateinit var newBook : Book

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater!!.inflate(R.layout.fragment_addbook_2, container, false)
        binding = DataBindingUtil.bind(view)
        selectedGenres = ArrayList<String>()

        FirebaseDatabase.getInstance().reference.child("genres")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(genSnap: DataSnapshot?) {
                        genres = Array(genSnap?.childrenCount!!.toInt(),{ i-> (genSnap.value as List<String>)[i]})
                        Log.e("GENRES:",genres.toString())
                        binding.genresChips.addChips(genres)
                        binding.loading.visibility = View.GONE
                    }
                })
        binding.genresChips.setChipListener(object: ChipListener {
            override fun chipSelected(p0: Int) {
                selectedGenres.add(genres[p0])
            }
            override fun chipDeselected(p0: Int) {
                selectedGenres.remove(genres[p0])
            }
        })

        binding.addBook.setOnClickListener {
            if(selectedGenres.size<1){
                Toast.makeText(context,"You must choose at least one genre.",Toast.LENGTH_SHORT).show()
            }
            else {
                newBook = Pref.addingBook
                if(Pref.fontChoice == "zawgyi"){
                    Log.e("Converting:","rom zawgyi to unicode")
                    newBook.title = Rabbit.zg2uni(newBook.title)
                    newBook.author = Rabbit.zg2uni(newBook.author)
                    newBook.description = Rabbit.zg2uni(newBook.description)
                }
                newBook.genres = selectedGenres
                addBook(newBook, Pref.addingCover)
            }
        }

        return view
    }
    fun addBook(book:Book, coverBitmap:Bitmap?){
        book.owner = FirebaseAuth.getInstance().currentUser!!.uid

        Log.e("GENS - ",book.genres.toString())
        val mDatabase = FirebaseDatabase.getInstance().reference.child("books")
        val key:String = mDatabase.push().key
        Pref.addingCover = null
        if(coverBitmap!=null) {
            val bitmap = coverBitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40 , baos)
            val data = baos.toByteArray()

            val storageRef = FirebaseStorage.getInstance().reference.child("books/$key.jpeg")

            val pd = ProgressDialog(activity)
            pd.setMessage("Adding book")
            pd.show()

            val uploadTask = storageRef.putBytes(data)
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    uri ->
                    book.bookCover = uri.toString()
                    val childUpdate = HashMap<String, Any>()
                    childUpdate.put(key, book.toHashMap())
                    mDatabase.updateChildren(childUpdate)
                    pd.hide()
                    (parentFragment as AddBookDialogFragment).dialog.hide()
                }
            }
        }
        else{
            val childUpdate = HashMap<String, Any>()
            childUpdate.put(key, book.toHashMap())
            mDatabase.updateChildren(childUpdate)
            (parentFragment as AddBookDialogFragment).dialog.hide()
        }
    }
}