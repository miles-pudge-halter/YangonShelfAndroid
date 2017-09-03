package com.zwenex.ayoe.yangonshelf

import android.app.Application
import android.graphics.Bitmap
import android.support.multidex.MultiDexApplication
import android.support.v7.app.AppCompatDelegate
import com.google.firebase.database.FirebaseDatabase
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.crashlytics.android.Crashlytics
import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import com.zwenex.ayoe.yangonshelf.Models.Book
import io.fabric.sdk.android.Fabric



/**
 * Created by ayoe on 7/21/17.
 */

class BookApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Logger.addLogAdapter(AndroidLogAdapter())
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Fabric.with(this, Crashlytics())
        PreferenceHolder.setContext(applicationContext)
    }
}
object Pref: PreferenceHolder() {
    var isRegistered: Boolean by bindToPreferenceField(false)
    var addingBook: Book by bindToPreferenceField(Book())
    var addingCover: Bitmap? = null
    var instanceId: String by bindToPreferenceField("")
    var fontChoice: String? by bindToPreferenceFieldNullable()
}
