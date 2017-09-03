package com.zwenex.ayoe.yangonshelf.Fragments


import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zwenex.ayoe.yangonshelf.Adapters.TradeListRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.Adapters.TradeViewPagerAdapter
import com.zwenex.ayoe.yangonshelf.Models.Trade
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.FragmentTradeBinding


/**
 * A simple [Fragment] subclass.
 */
class TradeFragment : Fragment() {

    lateinit var binding: FragmentTradeBinding
    lateinit var requestingTrades : ArrayList<Trade>
    lateinit var requestedTrades : ArrayList<Trade>



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var v = inflater!!.inflate(R.layout.fragment_trade, container, false)
        binding = DataBindingUtil.bind(v)

        requestingTrades = ArrayList<Trade>()
        requestedTrades = ArrayList<Trade>()

        val toolbar = binding.toolbar
        toolbar.title=""
        val toolbarTitle = binding.toolbarTitle
        toolbarTitle.textSize = 26f
        toolbarTitle.typeface = Typeface.createFromAsset(context.assets,"font/PoiretOne-Regular.ttf")
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val viewPager = binding.viewPager
        val adapter = TradeViewPagerAdapter(fragmentManager,2)
        viewPager.adapter = adapter
        val incomingBtn = binding.incomingBtn
        val outgoingBtn = binding.outgoingBtn
        incomingBtn.setOnClickListener {
            viewPager.setCurrentItem(0,true)
            incomingBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.button_bg_left))
            incomingBtn.setTextColor(Color.parseColor("#ffffff"))
            outgoingBtn.setBackgroundColor(Color.TRANSPARENT)
            outgoingBtn.setTextColor(Color.parseColor("#000000"))
        }
        outgoingBtn.setOnClickListener {
            viewPager.setCurrentItem(1, true)
            outgoingBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.button_bg_right))
            outgoingBtn.setTextColor(Color.parseColor("#ffffff"))
            incomingBtn.setBackgroundColor(Color.TRANSPARENT)
            incomingBtn.setTextColor(Color.parseColor("#000000"))
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                if(position==0){
                    incomingBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.button_bg_left))
                    incomingBtn.setTextColor(Color.parseColor("#ffffff"))
                    outgoingBtn.setBackgroundColor(Color.TRANSPARENT)
                    outgoingBtn.setTextColor(Color.parseColor("#000000"))
                }else{
                    outgoingBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.button_bg_right))
                    outgoingBtn.setTextColor(Color.parseColor("#ffffff"))
                    incomingBtn.setBackgroundColor(Color.TRANSPARENT)
                    incomingBtn.setTextColor(Color.parseColor("#000000"))
                }
            }

        })

        return v
    }


}// Required empty public constructor
