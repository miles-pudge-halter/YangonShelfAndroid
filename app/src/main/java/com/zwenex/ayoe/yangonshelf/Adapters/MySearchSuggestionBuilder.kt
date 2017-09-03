package com.zwenex.ayoe.yangonshelf.Adapters

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.cryse.widget.persistentsearch.SearchItem
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder

/**
 * Created by aYoe on 8/17/2017.
 */

class MySearchSuggestionBuilder (context : Context) : SearchSuggestionsBuilder {
    var mContext: Context
    var searchSuggestions : ArrayList<SearchItem>

    init{
        searchSuggestions = ArrayList<SearchItem>()
        this.mContext = context
        createSuggestion()
    }

    override fun buildEmptySearchSuggestion(maxCount: Int): Collection<SearchItem>? {
        val items = ArrayList<SearchItem>()
        items.addAll(searchSuggestions)
        return items
    }

    override fun buildSearchSuggestion(maxCount: Int, query: String): Collection<SearchItem>? {
        val items = ArrayList<SearchItem>()
        searchSuggestions.forEach { sugg->
            if(sugg.value.toLowerCase().startsWith(query.toLowerCase()))
                items.add(sugg)
        }
        return items
    }

    fun createSuggestion() {
        var genres : Array<String>? = null

        FirebaseDatabase.getInstance().reference.child("genres").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("Genres fetech failed",p0.toString())
            }
            override fun onDataChange(data: DataSnapshot?) {
                genres = Array<String>(data?.childrenCount!!.toInt(),{i-> (data.value as List<String>)[i]})
                genres?.forEach { g ->
                    val item = SearchItem("Genre: "+g,g,SearchItem.TYPE_SEARCH_ITEM_SUGGESTION)
                    searchSuggestions.add(item)
                }
                Log.e("Suggs",searchSuggestions.toString())
            }
        })
    }
}
