package com.zwenex.ayoe.yangonshelf.Trade

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zwenex.ayoe.yangonshelf.BR
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.FragmentTradeFragment1Binding
import com.zwenex.ayoe.yangonshelf.databinding.TradeBookCardBinding
import org.rabbitconverter.rabbit.Rabbit


/**
 * Created by ayoe on 7/27/17.
 */
class TradeRecyclerViewAdapter (mContext: Context, bookList: List<Book>, side:String,fragment : Fragment?) : RecyclerView.Adapter<TradeRecyclerViewAdapter.OurViewHolder>(){

    var clickListener: OnClickListener? = null
    var mContext: Context
    var side : String
    var bookList: List<Book>
    var selectedItems : ArrayList<String>
    var fragment : Fragment?
    init {
        this.mContext = mContext
        this.bookList = bookList
        this.side = side
        this.fragment = fragment
        selectedItems = ArrayList()
    }

    inner class OurViewHolder(view : View): RecyclerView.ViewHolder(view){
        var binding : TradeBookCardBinding
        init {
            binding = DataBindingUtil.bind(view)
            itemView.setOnClickListener {
                clickListener?.onClick(adapterPosition,itemView)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OurViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.trade_book_card,parent,false)
        return OurViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OurViewHolder, position: Int) {
        val book = bookList[position]
        Log.e("in adapter: ",book.toString())
        holder.binding.setVariable(BR.book,book)
        holder.binding.executePendingBindings()
        if(Pref.fontChoice=="zawgyi") {
            holder.binding.text2.text = Rabbit.uni2zg(book.author)
            holder.binding.text1.text = Rabbit.uni2zg(book.title)
        }
    }
    override fun getItemCount(): Int {
        return bookList.size
    }
    /***
     * Methods required for do selections, remove selections, etc.
     */
    fun  getItemAtPosition(position:Int) = bookList?.get(position)
    //Toggle selection methods
    fun toggleSelection(position: String) {
        selectView(position, !selectedItems.contains(position))
        Log.e("Selected Items: "+position,selectedItems.toString())
        var fragBinding : Any
        if(side=="frag1"){
            fragBinding = (fragment as TradeFragment1).binding
            fragBinding.bookCount.text = selectedItems.size.toString()+" books selected"
        }
        else{
            fragBinding = (fragment as TradeFragment2).binding
            fragBinding.bookCount.text = selectedItems.size.toString()+" books selected"
        }
    }

    //Remove selected selections
    fun removeSelection() {
        selectedItems = ArrayList()
    }


    //Put or delete selected position into SparseBooleanArray
    fun selectView(position: String, value: Boolean) {
        if (value){
            selectedItems.add(position)
        }
        else {
            selectedItems.remove(position)
        }
    }

    //Get total selected count
    fun getSelectedCount(): Int {
        return selectedItems.size
    }

    //Return all selected ids
    fun getSelectedIds(): ArrayList<String> {
        return selectedItems
    }
    interface OnClickListener {
        fun onClick(position: Int, view: View)
    }

}