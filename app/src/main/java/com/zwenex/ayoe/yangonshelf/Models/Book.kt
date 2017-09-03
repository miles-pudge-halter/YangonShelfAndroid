package com.zwenex.ayoe.yangonshelf.Models

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.Keep

/**
 * Created by ayoe on 7/12/17.
 */
@Keep
data class Book(
    var id: String?="",
    var title: String?="",
    var author: String?="",
    var description: String?="",
    var owner: String?="",
    var ownerObj: UserDetails?=null,
    var genres: ArrayList<String>?=null,
    var bookCover: String?="",
    var currentHolder: String?=null,
    var pending: Boolean = false
) :Parcelable{
    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        title = parcel.readString()
        author = parcel.readString()
        description = parcel.readString()
        owner = parcel.readString()
        ownerObj = parcel.readParcelable(UserDetails::class.java.classLoader)
        genres = parcel.createStringArrayList()
        bookCover = parcel.readString()
        currentHolder = parcel.readString()
        pending = parcel.readValue(Boolean::class.java.classLoader) as Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(description)
        parcel.writeString(owner)
        parcel.writeParcelable(ownerObj, flags)
        parcel.writeStringList(genres)
        parcel.writeString(bookCover)
        parcel.writeString(currentHolder)
        parcel.writeValue(pending)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }

    fun toHashMap() : HashMap<String,Any?>{
        val bookHashMap = HashMap<String, Any?> ()
        bookHashMap.put("title", title)
        bookHashMap.put("author", author)
        bookHashMap.put("description", description)
        bookHashMap.put("owner", owner)
        bookHashMap.put("genres", genres)
        bookHashMap.put("bookCover",bookCover)

        return bookHashMap
    }

}