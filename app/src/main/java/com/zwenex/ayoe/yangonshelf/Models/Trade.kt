package com.zwenex.ayoe.yangonshelf.Models

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.Keep

/**
 * Created by aYoe on 8/1/2017.
 */
@Keep
data class Trade(
        var id: String? = null,
        var requester: String? = null,
        var requester_obj: UserDetails? = null,
        var requestee: String? = null,
        var requestee_obj: UserDetails? = null,
        var requesterConfirm: Boolean? = null,
        var requesteeConfirm: Boolean? = null,
        var requester_fail: Boolean? = null,
        var requestee_fail: Boolean? = null,
        var requesting: ArrayList<String>? = null,
        var requesting_obj: ArrayList<Book>?=null,
        var giving: ArrayList<String>? = null,
        var giving_obj: ArrayList<Book>? = null,
        var status: String?=null,
        var timestamp: String?=null
):Parcelable{
    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        requester = parcel.readString()
        requester_obj = parcel.readParcelable(UserDetails::class.java.classLoader)
        requestee = parcel.readString()
        requestee_obj = parcel.readParcelable(UserDetails::class.java.classLoader)
        requesterConfirm = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        requesteeConfirm = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        requester_fail = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        requestee_fail = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        status = parcel.readString()
        requesting = parcel.createStringArrayList()
        giving = parcel.createStringArrayList()
        timestamp = parcel.readString()
    }

    fun toHashMap(): HashMap<String, Any?> {
        val tradeHashMap = HashMap<String, Any?>()
        tradeHashMap.put("requester", requester)
        tradeHashMap.put("requestee", requestee)
        tradeHashMap.put("requesterConfirm", requesterConfirm)
        tradeHashMap.put("requesteeConfirm", requesteeConfirm)
        tradeHashMap.put("requester_fail", requester_fail)
        tradeHashMap.put("requestee_fail", requestee_fail)
        tradeHashMap.put("giving",giving)
        tradeHashMap.put("requesting",requesting)
        tradeHashMap.put("status",status)
        tradeHashMap.put("timestamp",timestamp)
        return tradeHashMap
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(requester)
        parcel.writeParcelable(requester_obj, flags)
        parcel.writeString(requestee)
        parcel.writeParcelable(requestee_obj, flags)
        parcel.writeValue(requesterConfirm)
        parcel.writeValue(requesteeConfirm)
        parcel.writeValue(requester_fail)
        parcel.writeValue(requestee_fail)
        parcel.writeString(status)
        parcel.writeStringList(giving)
        parcel.writeStringList(requesting)
        parcel.writeString(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trade> {
        override fun createFromParcel(parcel: Parcel): Trade {
            return Trade(parcel)
        }

        override fun newArray(size: Int): Array<Trade?> {
            return arrayOfNulls(size)
        }
    }
}