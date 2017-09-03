package com.zwenex.ayoe.yangonshelf.Fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.zwenex.ayoe.yangonshelf.Adapters.MySearchSuggestionBuilder
import com.zwenex.ayoe.yangonshelf.Adapters.ViewPagerAdapter
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.SearchActivity
import com.zwenex.ayoe.yangonshelf.databinding.FragmentFeedBinding
import org.cryse.widget.persistentsearch.PersistentSearchView

class FeedFragment : Fragment() {

    lateinit var binding : FragmentFeedBinding
    var pager : ViewPager? = null
    var adapter: ViewPagerAdapter? = null
    var Titles = arrayOf<CharSequence>("Following", "All")
    lateinit var searchView : PersistentSearchView


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v:View =  inflater!!.inflate(R.layout.fragment_feed, container, false)

        binding = DataBindingUtil.bind(v)
        adapter = ViewPagerAdapter(activity.supportFragmentManager,Titles,2)
        pager = binding.feedPager

        val tabs = binding.feedTabs

        pager!!.adapter= adapter
        tabs.setupWithViewPager(pager)
        tabs.setBackgroundColor(resources.getColor(R.color.colorPrimary))


        pager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        val fab = v.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener({ view ->
            val newBookDialog : DialogFragment = AddBookDialogFragment()
            newBookDialog.show(fragmentManager,"fragment_add_book_dialog")
        })
        val toolbar = binding.toolbar
        toolbar.title = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.elevation = 0f
        }
        val toolbarTitle = binding.toolbarTitle
        toolbarTitle.textSize = 26f
        toolbarTitle.typeface = Typeface.createFromAsset(context.assets,"font/PoiretOne-Regular.ttf")

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        searchView = binding.searchView
        searchView.setSuggestionBuilder(MySearchSuggestionBuilder(context))
        searchView.setSearchListener(object : PersistentSearchView.SearchListener{
            override fun onSearch(query: String?) {
                val intent = Intent(context, SearchActivity::class.java)
                intent.putExtra("query",query)
                startActivity(intent)
            }
            override fun onSearchExit() {
            }
            override fun onSearchCleared() {
            }
            override fun onSearchEditClosed() {
            }
            override fun onSearchTermChanged(term: String?) {
            }
            override fun onSearchEditBackPressed(): Boolean {
                searchView.closeSearch()
                return true
            }
            override fun onSearchEditOpened() {
            }
        })
        return v
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.home, menu)

        val item = menu!!.findItem(R.id.action_search)
        item.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener{
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                searchView.openSearch()
                return true
            }

        })
    }
}// Required empty public constructor
