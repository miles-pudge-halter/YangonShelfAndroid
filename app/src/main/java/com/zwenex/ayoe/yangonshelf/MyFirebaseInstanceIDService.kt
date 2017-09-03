package com.zwenex.ayoe.yangonshelf

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by aYoe on 8/16/2017.
 */
class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val instance_id = FirebaseInstanceId.getInstance().token
        Log.e("Token refresh", instance_id)
            Pref.instanceId = instance_id!!
//            Handler(Looper.getMainLooper()).postDelayed({
//
//            }, 1000)

    }

}