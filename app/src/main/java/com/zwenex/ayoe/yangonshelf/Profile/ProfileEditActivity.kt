package com.zwenex.ayoe.yangonshelf.Profile

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zwenex.ayoe.yangonshelf.BR
import com.zwenex.ayoe.yangonshelf.Models.Township
import com.zwenex.ayoe.yangonshelf.Models.UserDetails

import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.ActivityProfileEditBinding
import android.widget.ArrayAdapter
import com.zwenex.ayoe.yangonshelf.Adapters.MySpinnerAdapter


class ProfileEditActivity : AppCompatActivity() {

    lateinit var binding : ActivityProfileEditBinding
    lateinit var tsList : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile_edit)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        var locRef = FirebaseDatabase.getInstance().getReference("Township")
        locRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) { Log.e("genres fetch fail:",p0.toString()) }

            override fun onDataChange(snapshot: DataSnapshot) {
                getUserInfo(uid)
                tsList = ArrayList<String>()
                snapshot.children
                        .map { it.getValue(Township::class.java) as Township }
                        .mapTo(tsList) { it.name+" ("+ it.name_mm+")" }
                val dataAdapter = MySpinnerAdapter(applicationContext,R.layout.custom_spinner_style,tsList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.editAddress.adapter = dataAdapter

                //binding.editAddress.attachDataSource(townships)
            }

        })

        binding.editSubmit.setOnClickListener {
            updateUserInfo(binding.editName.text.toString(),binding.editPhone.text.toString()
                    ,binding.editAddress.selectedItem.toString(),binding.editQuote.text.toString(),uid)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getUserInfo(uid:String){
        val uDatabase = FirebaseDatabase.getInstance().getReference("users/"+uid)
        uDatabase.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("fetch fail at all", p0.toString())
            }

            override fun onDataChange(userSnap: DataSnapshot?) {
                val fetchedUser : UserDetails = userSnap!!.getValue(UserDetails::class.java)
                binding.setVariable(BR.user,fetchedUser)
                binding.editAddress.setSelection(tsList.indexOf(fetchedUser.address))
            }
        })
    }

    fun updateUserInfo(name:String, phone:String, address:String, quote:String,uid:String){
        val updateUser : UserDetails = UserDetails()
        updateUser.displayName = name
        updateUser.phone = phone
        updateUser.address = address
        updateUser.quote = quote
        val childUpdate = HashMap<String,Any>()
        childUpdate.put(uid,updateUser.toHashMap())
        val mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        mDatabase.child("displayName").setValue(name)
        mDatabase.child("phone").setValue(phone)
        mDatabase.child("address").setValue(address)
        mDatabase.child("quote").setValue(quote)
        onBackPressed()
    }

}
