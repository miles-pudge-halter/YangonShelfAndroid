package com.zwenex.ayoe.yangonshelf.Models

import android.support.annotation.Keep

/**
 * Created by ayoe on 7/17/17.
 */
@Keep
data class Notification(
        var id :String?=null,
        var requester: String?=null,
        var requesterObj: UserDetails?=null,
        var tradeID: String?=null,
        var tradeObj: Trade?=null,
        var timestamp: String?=null,
        var type: String?=null,
        var seen: Boolean?=null
){
    fun toHashMap(): HashMap<String,Any?>{
        val notiHashMap =  HashMap<String,Any?>()

        notiHashMap.put("requester",requester)
        notiHashMap.put("tradeID",tradeID)
        notiHashMap.put("timestamp",timestamp)
        notiHashMap.put("type",type)
        notiHashMap.put("seen",seen)

        return notiHashMap
    }
}

