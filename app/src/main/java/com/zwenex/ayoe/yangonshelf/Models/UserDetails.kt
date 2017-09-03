package com.zwenex.ayoe.yangonshelf.Models

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.Keep

/**
 * Created by ayoe on 7/17/17.
 */
@Keep
data class UserDetails(
    var displayName: String?=null,
    var email: String?=null,
    var address: String?=null,
    var phone: String?=null,
    var fbID: String?=null,
    var photoURL: String?=null,
    var quote: String?=null,
    var genres: List<String>?=null,
    var instanceID:String?=null
): Parcelable{

    constructor(parcel: Parcel) : this() {
        displayName = parcel.readString()
        email = parcel.readString()
        address = parcel.readString()
        phone = parcel.readString()
        fbID = parcel.readString()
        photoURL = parcel.readString()
        quote = parcel.readString()
        genres = parcel.createStringArrayList()
        instanceID = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(displayName)
        parcel.writeString(email)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeString(fbID)
        parcel.writeString(photoURL)
        parcel.writeString(quote)
        parcel.writeStringList(genres)
        parcel.writeString(instanceID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDetails> {
        override fun createFromParcel(parcel: Parcel): UserDetails {
            return UserDetails(parcel)
        }

        override fun newArray(size: Int): Array<UserDetails?> {
            return arrayOfNulls(size)
        }
    }
    fun toHashMap() : HashMap<String,Any?>{
        val userHashMap = HashMap<String, Any?> ()
        userHashMap.put("displayName",displayName)
        userHashMap.put("email",email)
        userHashMap.put("phone",phone)
        userHashMap.put("address",address)
        userHashMap.put("quote",quote)
        userHashMap.put("genres",genres)
        userHashMap.put("fbID",fbID)
        userHashMap.put("photoURL",photoURL)
        userHashMap.put("instanceID",instanceID)

        return userHashMap
    }
}