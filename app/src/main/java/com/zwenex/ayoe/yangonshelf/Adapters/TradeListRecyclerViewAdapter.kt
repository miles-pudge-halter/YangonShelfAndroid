package com.zwenex.ayoe.yangonshelf.Adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zwenex.ayoe.yangonshelf.Models.Trade
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.TradeListLayoutBinding
import org.rabbitconverter.rabbit.Rabbit
import java.text.SimpleDateFormat
import java.util.*


class TradeListRecyclerViewAdapter(mContext: Context,tradeList: ArrayList<Trade>) : RecyclerView.Adapter<TradeListRecyclerViewAdapter.MyViewHolder>(){


    var clickListener: OnClickListener? = null
    var mContext: Context
    var tradeList: List<Trade>? = null


    override fun getItemCount(): Int {
        return tradeList!!.size
    }
    init {
        this.mContext = mContext
        this.tradeList = tradeList
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding : TradeListLayoutBinding
        var traderProfile : ImageView
        var tradeBody : TextView
        var tradeStatus : ImageView
        var tradeTimestamp: TextView
        init {
            binding = DataBindingUtil.bind(view)
            traderProfile = binding.traderProfile
            tradeBody = binding.tradeBody
            tradeStatus = binding.tradeStatus
            tradeTimestamp = binding.tradeTimestamp
            itemView.setOnClickListener {
                clickListener?.onClick(adapterPosition,itemView)
            }
        }
    }
    fun  getItemAtPosition(position:Int) = tradeList!![position]
    fun getAllItems() = this.tradeList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.trade_list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //Collections.reverse(tradeList)
        val trade = tradeList!![position]
        if(trade.status == "failed")
            holder.tradeStatus.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_failed))
        else if(trade.status == "accepted")
            holder.tradeStatus.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_accepted))
        else if(trade.status == "confirmed")
            holder.tradeStatus.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_complete))
        else if(trade.status == "denied")
            holder.tradeStatus.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_denied))
        else if(trade.status.isNullOrEmpty())
            holder.tradeStatus.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_compare_blue))
        val date = Date(trade.timestamp)
        val simpleDateFormat = SimpleDateFormat("MMMM dd, yyyy - hh:mm a")
        val timestamp = simpleDateFormat.format(date)
        holder.tradeTimestamp.text = timestamp.toString()

        holder.tradeBody.typeface = Typeface.createFromAsset(mContext.assets,"font/NotoSansMyanmarUI-Regular.ttf")
        if(trade.requestee_obj!=null) {
            Glide.with(mContext)
                    .load("http://graph.facebook.com/" + trade.requestee_obj!!.fbID + "/picture?width=400")
                    .crossFade().into(holder.traderProfile)
            val requestedList = ArrayList<String>()
            trade.requesting_obj!!.forEach { book -> requestedList.add(book.title!!) }
            if(Pref.fontChoice=="zawgyi")
                holder.tradeBody.text = Html.fromHtml(Rabbit.uni2zg("You requested <font color=\"#4285F4\">"+ requestedList.joinToString(", ")+"</font> from <b>"+ trade.requestee_obj!!.displayName+"</b>."))
            else
                holder.tradeBody.text = Html.fromHtml("You requested <font color=\"#4285F4\">"+ requestedList.joinToString(", ")+"</font> from <b>"+ trade.requestee_obj!!.displayName+"</b>.")
        }
        else {
            Glide.with(mContext)
                    .load("http://graph.facebook.com/" + trade.requester_obj!!.fbID + "/picture?width=400")
                    .crossFade().into(holder.traderProfile)
            val givingList = ArrayList<String>()
            trade.giving_obj!!.forEach { book -> givingList.add(book.title!!) }
            if(Pref.fontChoice=="zawgyi")
                holder.tradeBody.text = Html.fromHtml(Rabbit.uni2zg("<b>"+trade.requester_obj!!.displayName+"</b> requested <font color=\"#4285F4\">"+ givingList.joinToString(", ")+"</font> from you."))
            else
                holder.tradeBody.text = Html.fromHtml("<b>"+trade.requester_obj!!.displayName+"</b> requested <font color=\"#4285F4\">"+ givingList.joinToString(", ")+"</font> from you.")
        }
    }
    interface OnClickListener {
        fun onClick(position: Int, view: View)
    }
}
