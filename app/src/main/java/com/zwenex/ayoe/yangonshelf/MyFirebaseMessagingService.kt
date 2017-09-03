package com.zwenex.ayoe.yangonshelf

/**
 * Created by aYoe on 8/16/2017.
 */
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.support.v7.app.NotificationCompat
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zwenex.ayoe.yangonshelf.Models.Trade


/**
 * Created by mt on 7/26/17.
 */
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.e("NOTI", remoteMessage!!.data.toString())

        sendNotification(remoteMessage)
    }

    fun sendNotification(remoteMessage: RemoteMessage) {

        val intent = Intent(this, ViewTradeActivity::class.java)
        val trade = Trade()
        trade.id = remoteMessage.notification.tag
        intent.putExtra("trade",trade)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT)

        val bitmap = getBitmapFromURL(remoteMessage.notification.icon)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setSound(defaultSoundUri)
                .setContentTitle(remoteMessage.notification.title.toString())
                .setContentText(remoteMessage.notification.body)
                .setAutoCancel(true)


                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


    fun getBitmapFromURL(src: String): Bitmap? {
        return Glide.with(this).load(src).asBitmap().into(100,100).get()
    }
}