package com.zwenex.ayoe.yangonshelf.Fragments

import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zwenex.ayoe.yangonshelf.BR
import com.zwenex.ayoe.yangonshelf.MainActivity
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.Profile.ProfileActivity
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.Trade.TradeActivity
import com.zwenex.ayoe.yangonshelf.databinding.FragmentBookDialogBinding
import org.apmem.tools.layouts.FlowLayout
import org.rabbitconverter.rabbit.Rabbit

/**
 * Created by ayoe on 7/24/17.
 */
class BooksPagerFragment (var book:Book,var editable:Boolean) : Fragment(){

    lateinit var binding : FragmentBookDialogBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_book_dialog,container,false)
        binding = DataBindingUtil.bind(view)


        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(BR.book,book)
        binding.executePendingBindings()
        if(Pref.fontChoice == "zawgyi") {
            binding.dialogBooktitle.text = Rabbit.uni2zg(book.title)
            binding.dialogAuthor.text = Rabbit.uni2zg(book.author)
            binding.dialogDesc.text = Rabbit.uni2zg(book.description)
            binding.dialogOwner.text = Html.fromHtml("Owner: " + Rabbit.uni2zg(book.ownerObj?.displayName))
        }
        else
            binding.dialogOwner.text = Html.fromHtml("Owner: " + book.ownerObj?.displayName)

        val dialogFlowLayout = binding.dialogLinearLayout
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if(editable && book.owner == currentUid){
            if(book.currentHolder!=null){
                binding.delBtn.visibility = View.INVISIBLE
                binding.returnBtn.visibility = View.VISIBLE
            }
            else{
                binding.delBtn.visibility = View.VISIBLE
                binding.returnBtn.visibility = View.INVISIBLE
            }
        }
        for(genre in book.genres!!){
            val tags : TextView = TextView(context)
            tags.text = genre
            tags.setTextColor(Color.parseColor("#111111"))
            tags.background = ContextCompat.getDrawable(context,R.drawable.textview_bg)
            tags.textSize = 10f
            val params = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(5, 5, 5, 5)
            tags.layoutParams = params
            dialogFlowLayout.addView(tags)
        }
        if(FirebaseAuth.getInstance().currentUser!!.uid == book.owner || !book.currentHolder.isNullOrBlank() || book.pending)
            binding.dialogRequestBtn.isEnabled = false

        binding.dialogOwner.setOnClickListener {
            val intent = Intent(activity,ProfileActivity::class.java)
            intent.putExtra("uid",book.owner)
            startActivity(intent)
        }
        binding.dialogCancelBtn.setOnClickListener {

        }
        binding.dialogRequestBtn.setOnClickListener {
            val intent = Intent(activity, TradeActivity::class.java)
            intent.putExtra("book",book)
            startActivity(intent)
        }
        binding.returnBtn.setOnClickListener {
            AlertDialog.Builder(context)
                    .setTitle("Receive book")
                    .setMessage(Html.fromHtml("Have you received <b>${book.title}</b> from the person you borrowed to? Only click yes if you have received the book."))
                    .setPositiveButton("Yes", { dialog, which ->
                        FirebaseDatabase.getInstance().reference.child("books").child(book.id).child("currentHolder").removeValue().addOnCompleteListener {
                            (parentFragment as BookDialogFragment).dismiss()
                        }
                    })
                    .setNegativeButton("No",null).show()
        }
        binding.delBtn.setOnClickListener {
            AlertDialog.Builder(context)
                    .setTitle("Delete book")
                    .setMessage(Html.fromHtml("Are you sure you want to delete <b>${book.title}</b>?"))
                    .setPositiveButton("Yes", { dialog, which ->
                        FirebaseDatabase.getInstance().reference.child("books").child(book.id).removeValue().addOnCompleteListener {
                            (parentFragment as BookDialogFragment).dismiss()
                        }
                    })
                    .setNegativeButton("No",null).show()
        }

    }
}