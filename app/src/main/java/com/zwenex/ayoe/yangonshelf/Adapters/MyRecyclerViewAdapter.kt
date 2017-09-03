package com.zwenex.ayoe.yangonshelf.Adapters

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.jaredrummler.fastscrollrecyclerview.FastScrollRecyclerView
import com.zwenex.ayoe.yangonshelf.BR
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.BookCardBinding
import org.apmem.tools.layouts.FlowLayout
import org.rabbitconverter.rabbit.Rabbit


class MyRecyclerViewAdapter(mContext: Context,bookList: List<Book>,editable:Boolean) : RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>(),
        FastScrollRecyclerView.SectionedAdapter {
    override fun getSectionName(position: Int): String {
        return bookList!![position].title!!.substring(0, 1).toUpperCase()
    }

    var clickListener: OnClickListener? = null
    var mContext: Context? = null
    var bookList: List<Book>? = null
    var editable = false

    override fun getItemCount(): Int {

        return bookList!!.size
    }
    init {
        this.editable = editable
        this.mContext = mContext
        this.bookList = bookList
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        var author: TextView
        var bookCover: ImageView
        var  binding:BookCardBinding
        var card : CardView

        init {
            title = view.findViewById(R.id.text1) as TextView
            author = view.findViewById(R.id.text2) as TextView
            bookCover = view.findViewById(R.id.imageView2) as ImageView
            card = view.findViewById(R.id.card_view) as CardView
            binding = DataBindingUtil.bind(view)
            itemView.setOnClickListener {
                clickListener?.onClick(adapterPosition,itemView)
            }
        }
    }
    fun  getItemAtPosition(position:Int) = bookList?.get(position)
    fun getAllItems() = this.bookList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_card, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val book = bookList!![position]
        holder.binding.setVariable(BR.book,book)
        holder.binding.executePendingBindings()
        //book.title = Rabbit.uni2zg(book.title)
        if(Pref.fontChoice == "zawgyi") {
            holder.binding.text1.text = Rabbit.uni2zg(book.title)
            holder.binding.text2.text = Rabbit.uni2zg(book.author)
        }
//        holder.title.typeface = Typeface.createFromAsset(mContext!!.assets,"font/NotoSansMyanmarUI-Bold.ttf")
//        holder.author.typeface = Typeface.createFromAsset(mContext!!.assets,"font/NotoSansMyanmarUI-Regular.ttf")
//        mmtext.prepareView(mContext,holder.title,mmtext.TEXT_UNICODE,true,false)
//        mmtext.prepareView(mContext,holder.author,mmtext.TEXT_UNICODE,true,false)

        val dialogFlowLayout = holder.binding.dialogFlowLayout
        dialogFlowLayout.removeAllViews()
        for(genre in book.genres!!){
            val tags : TextView = TextView(mContext)
            tags.text = genre
            tags.setTextColor(Color.parseColor("#00BEBA"))
            tags.background = ContextCompat.getDrawable(mContext,R.drawable.textview_bg)
            tags.textSize = 10f
            val params = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(5, 5, 5, 5)

            tags.layoutParams = params

            dialogFlowLayout.addView(tags)
        }


    }
    interface OnClickListener {
        fun onClick(position: Int, view: View)
    }
}
class DataBindingHelpers {
    companion object {
        @BindingAdapter("bind:image")
        @JvmStatic fun image(view: ImageView, url: String) {
            Glide.with(view.context).load(url).placeholder(R.drawable.kafka).crossFade().into(view)
        }
        fun <T> reverse(list: ArrayList<T>): ArrayList<T> {
            if (list.size > 1) {
                val value = list.removeAt(0)
                reverse(list)
                list.add(value)
            }
            return list
        }
    }
}
