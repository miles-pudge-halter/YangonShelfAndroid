package com.zwenex.ayoe.yangonshelf

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.zwenex.ayoe.yangonshelf.Adapters.MySpinnerAdapter
import com.zwenex.ayoe.yangonshelf.Models.Township
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    lateinit var binding : ActivityRegisterBinding
    lateinit var tsList : ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register)

        binding.inputName.editText?.setText(FirebaseAuth.getInstance().currentUser?.displayName)
        binding.inputEmail.editText?.setText(FirebaseAuth.getInstance().currentUser?.email)

        val locRef = FirebaseDatabase.getInstance().getReference("Township")
        locRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) { Log.e("genres fetch fail:",p0.toString()) }
            override fun onDataChange(snapshot: DataSnapshot) {
                tsList = ArrayList<String>()
                snapshot.children
                        .map { it.getValue(Township::class.java) as Township }
                        .mapTo(tsList) { it.name+" ("+ it.name_mm+")" }
                val dataAdapter = MySpinnerAdapter(applicationContext,R.layout.custom_spinner_style,tsList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.inputAddress.adapter = dataAdapter

                //binding.editAddress.attachDataSource(townships)
            }

        })
        binding.submitRegister.setOnClickListener {
            if(!isValidName(binding.inputName.editText?.text.toString())){
                binding.inputName.error = "Not a valid name."
            }
            else if(!isValidPhone(binding.inputPhone.editText?.text.toString())){
                binding.inputPhone.error = "Not a valid phone number."
            }
            else {
                val intent = Intent(this, GenresSelectorActivity::class.java)
                val newUser = UserDetails()
                newUser.displayName = binding.inputName.editText?.text.toString()
                newUser.email = binding.inputEmail.editText?.text.toString()
                newUser.phone = binding.inputPhone.editText?.text.toString()
                newUser.address = binding.inputAddress.selectedItem.toString()
                newUser.photoURL = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
                newUser.fbID = FirebaseAuth.getInstance().currentUser!!.providerData[1].uid
                newUser.instanceID = Pref.instanceId
                intent.putExtra("newUser", newUser)
                startActivity(intent)
                finish()
            }
        }
    }
    fun isValidName(name:String):Boolean{
        return name.length>5
    }
    fun isValidPhone(phone:String):Boolean{
        if(phone.length>6) {
            val phUtil = PhoneNumberUtil.getInstance()
            val parseNumber = phUtil.parse(phone, "MM")
            Log.e("New phone number", "+${parseNumber.countryCode} ${parseNumber.nationalNumber}")
            if(phUtil.isValidNumber(parseNumber)){
                binding.inputPhone.editText?.setText("+${parseNumber.countryCode} ${parseNumber.nationalNumber}")
                return true
            }else return false
        }
        return false
    }
    override fun onBackPressed() {
    }
}
