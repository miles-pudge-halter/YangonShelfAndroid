package com.zwenex.ayoe.yangonshelf.Profile

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.zwenex.ayoe.yangonshelf.Adapters.MySpinnerAdapter
import com.zwenex.ayoe.yangonshelf.BR
import com.zwenex.ayoe.yangonshelf.Models.Township
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.ActivityProfileEditBinding
import org.rabbitconverter.rabbit.Rabbit


/**
 * Created by ayoe on 7/26/17.
 */
class ProfileEditSheet : BottomSheetDialogFragment() {

    private val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }

        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    lateinit var binding: ActivityProfileEditBinding
    lateinit var tsList: ArrayList<String>

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        var v = View.inflate(context, R.layout.activity_profile_edit,null)
        binding = DataBindingUtil.bind(v)
        dialog.setContentView(v)
        val params = (v.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        ////
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val locRef = FirebaseDatabase.getInstance().getReference("Township")
        locRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) { Log.e("genres fetch fail:",p0.toString()) }

            override fun onDataChange(snapshot: DataSnapshot) {
                getUserInfo(uid)
                tsList = ArrayList<String>()
                snapshot.children
                        .map { it.getValue(Township::class.java) as Township }
                        .mapTo(tsList) { it.name+" ("+ it.name_mm+")" }
                val dataAdapter = MySpinnerAdapter(context,R.layout.custom_spinner_style,tsList)
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.editAddress.adapter = dataAdapter
                binding.editSubmit.setOnClickListener {
                    if(!isValidName(binding.editName.text.toString())){
                        binding.nameLayout.error = "Invalid name."
                    }
                    else if(!isValidPhone(binding.editPhone.text.toString())){
                        binding.phoneLayout.error= "Invalid phone number."
                    }
                    else {
                        updateUserInfo(binding.editName.text.toString(), binding.editPhone.text.toString()
                                , binding.editAddress.selectedItem.toString(), binding.editQuote.text.toString(), uid)
                    }
                }

                //binding.editAddress.attachDataSource(townships)
            }

        })
    }
    fun isValidName(name:String):Boolean{
        return name.length>1
    }
    fun isValidPhone(phone:String):Boolean{
        if(phone.length>6) {
            val phUtil = PhoneNumberUtil.getInstance()
            val parseNumber = phUtil.parse(phone, "MM")
            Log.e("New phone number", "+${parseNumber.countryCode} ${parseNumber.nationalNumber}")
            if(phUtil.isValidNumber(parseNumber)){
                binding.phoneLayout.editText?.setText("+${parseNumber.countryCode} ${parseNumber.nationalNumber}")
                return true
            }else return false
        }
        return false
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
                binding.executePendingBindings()
                if(Pref.fontChoice=="zawgyi"){
                    binding.editName.setText(Rabbit.uni2zg(fetchedUser.displayName))
                    if(fetchedUser.quote!=null)
                        binding.editQuote.setText(Rabbit.uni2zg(fetchedUser.quote))
                }
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
        if(Pref.fontChoice == "zawgyi"){
            Log.e("Font status","is zawgyi")
            updateUser.displayName = Rabbit.zg2uni(updateUser.displayName)
            updateUser.quote = Rabbit.zg2uni(updateUser.quote)
        }
        val childUpdate = HashMap<String,Any>()
        childUpdate.put(uid,updateUser.toHashMap())
        val mDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        mDatabase.child("displayName").setValue(updateUser.displayName)
        mDatabase.child("phone").setValue(updateUser.phone)
        mDatabase.child("address").setValue(updateUser.address)
        mDatabase.child("quote").setValue(updateUser.quote)
        dismiss()
    }
}