package com.zwenex.ayoe.yangonshelf.Adapters

import android.content.Context
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.Notification
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.NotiCardBinding
import org.rabbitconverter.rabbit.Rabbit
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ayoe on 7/17/17.
 */
class NotiRecyclerViewAdapter(mContext: Context, notiList: ArrayList<Notification>) : RecyclerView.Adapter<NotiRecyclerViewAdapter.MyNotiViewHolder>() {

    var clickListener: OnClickListener?=null
    var mContext: Context? = null
    var notiList: ArrayList<Notification>? = null


    override fun getItemCount(): Int {
        return notiList!!.size
    }
    init {
        this.mContext = mContext
        this.notiList = notiList
    }
    inner class MyNotiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var notiBody :TextView
        var notiStatus : ImageView
        var notiProfile: ImageView
        var binding: NotiCardBinding
        var timestamp : TextView
        init {
            binding = DataBindingUtil.bind(view)
            notiBody = binding.notiBody
            notiProfile = binding.notiProfile
            notiStatus = binding.notiStatus
            timestamp = binding.notiTimestamp
            itemView.setOnClickListener {
                clickListener?.onClick(adapterPosition,itemView)
            }
        }
    }
    fun  getItemAtPosition(position:Int) = notiList!![position]
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiRecyclerViewAdapter.MyNotiViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.noti_card, parent, false)

        return MyNotiViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: NotiRecyclerViewAdapter.MyNotiViewHolder, position: Int) {
        val noti = notiList?.get(position)
        if(noti?.timestamp!=null) {
            val date = Date(noti.timestamp)
            val simpleDateFormat = SimpleDateFormat("MMMM dd, yyyy - hh:mm aaa")
            val timestamp = simpleDateFormat.format(date)
            holder.timestamp.text = timestamp.toString()
        }

        val requestingBooks = ArrayList<String>()
        noti?.tradeObj?.requesting_obj?.forEach{book->
            requestingBooks.add(book.title!!)
        }
        val givingBooks = ArrayList<String>()
        noti?.tradeObj?.giving_obj?.forEach{book->
            givingBooks.add(book.title!!)
        }
        if(noti?.type == "accept") {
            Glide.with(mContext).load("http://graph.facebook.com/" + noti.tradeObj?.requestee_obj?.fbID+ "/picture?type=large").into(holder.notiProfile)
            if(Pref.fontChoice=="zawgyi")
                holder.notiBody.text = Html.fromHtml(Rabbit.uni2zg("<b>${noti.tradeObj?.requestee_obj?.displayName}</b> accepted your trade request for <font color=\"#4285F4\">${requestingBooks.joinToString(", ")}</font>"))
            else
                holder.notiBody.text = Html.fromHtml("<b>${noti.tradeObj?.requestee_obj?.displayName}</b> accepted your trade request for <font color=\"#4285F4\">${requestingBooks.joinToString(", ")}</font>")
            holder.notiStatus.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.ic_complete))
        }
        else if(noti?.type == "request") {
            Glide.with(mContext).load("http://graph.facebook.com/" + noti.tradeObj?.requester_obj?.fbID+ "/picture?type=large").into(holder.notiProfile)
            if(Pref.fontChoice=="zawgyi")
                holder.notiBody.text = Html.fromHtml(Rabbit.uni2zg("<b>${noti.requesterObj?.displayName}</b> requested <font color=\"#4285F4\">${requestingBooks.joinToString(", ")}</font> from you"))
            else
                holder.notiBody.text = Html.fromHtml("<b>${noti.requesterObj?.displayName}</b> requested <font color=\"#4285F4\">${requestingBooks.joinToString(", ")}</font> from you")
            holder.notiStatus.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.ic_compare_blue))
        }
        else if(noti?.type == "deny") {
            Glide.with(mContext).load("http://graph.facebook.com/" + noti.tradeObj?.requestee_obj?.fbID+ "/picture?type=large").into(holder.notiProfile)
            if(Pref.fontChoice=="zawgyi")
                holder.notiBody.text = Html.fromHtml(Rabbit.uni2zg("<b>${noti.tradeObj?.requestee_obj?.displayName}</b> denied your trade request for <font color=\"#4285F4\">${requestingBooks.joinToString(", ")}</font>"))
            else
                holder.notiBody.text = Html.fromHtml("<b>${noti.tradeObj?.requestee_obj?.displayName}</b> denied your trade request for <font color=\"#4285F4\">${requestingBooks.joinToString(", ")}</font>")
            holder.notiStatus.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.ic_denied))
        }
    }
    interface OnClickListener {
        fun onClick(position: Int, view: View)
    }
}