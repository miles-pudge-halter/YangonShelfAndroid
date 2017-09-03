package com.zwenex.ayoe.yangonshelf.Adapters

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Created by ayoe on 7/26/17.
 */
class MySpinnerAdapter(context: Context, resource: Int, objects: MutableList<String>) : ArrayAdapter<String>(context, resource, objects) {

    var mContext:Context
    var mResource:Int
    var mObjs:MutableList<String>
    init{
        this.mContext = context
        this.mResource = resource
        this.mObjs = objects
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.typeface = Typeface.createFromAsset(mContext.assets,"font/NotoSansMyanmar-Regular.ttf")
        view.textSize = 16f
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.typeface = Typeface.createFromAsset(mContext.assets,"font/NotoSansMyanmar-Regular.ttf")
        view.textSize = 16f
        return view
    }

}