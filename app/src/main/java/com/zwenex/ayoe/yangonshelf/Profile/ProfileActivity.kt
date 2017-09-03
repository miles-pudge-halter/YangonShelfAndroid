package com.zwenex.ayoe.yangonshelf.Profile

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.BR
import com.zwenex.ayoe.yangonshelf.Fragments.GenresSelectorFragment
import com.zwenex.ayoe.yangonshelf.MainActivity
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.Pref
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.ActivityProfileBinding
import jp.wasabeef.glide.transformations.BlurTransformation
import org.apmem.tools.layouts.FlowLayout
import org.rabbitconverter.rabbit.Rabbit


class ProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityProfileBinding
    lateinit var toolbar : Toolbar
    lateinit var uid:String
    lateinit var  bottomSheet : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(Color.parseColor("#000000"))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#000000"))
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val btmSheetFragment = ProfileEditSheet()
            btmSheetFragment.show(supportFragmentManager,btmSheetFragment.tag)
        }
        binding.contentProfile.logoutButton.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("Log out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", { dialog, which ->
                        Pref.isRegistered = false
                        FirebaseAuth.getInstance().signOut()
                        LoginManager.getInstance().logOut()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                    .setNegativeButton("No",null).show()
        }
         uid = intent.getStringExtra("uid")
        if(uid!=FirebaseAuth.getInstance().currentUser!!.uid){
            Log.e("Profile","tu buu")
            fab.visibility = View.GONE
            binding.contentProfile.phoneLayout.visibility = View.GONE
            binding.contentProfile.editFollow.visibility = View.GONE
            binding.contentProfile.yoursLabel.text = "Owned Books"
        }else binding.contentProfile.logoutButton.visibility = View.VISIBLE

        prepareUserData(uid)

        binding.contentProfile.yourBookButton.setOnClickListener {
            val intent = Intent(this,YourBooksActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)
        }
        binding.contentProfile.borrowedBookButton.setOnClickListener {
            val intent = Intent(this, BorrowedBooksActivity::class.java)
            intent.putExtra("uid",uid)
            startActivity(intent)
        }

//        bottomSheet = binding.bottomSheet
//        var bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            123 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareUserData(uid)
                } else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    fun prepareUserData(uid:String){
        val uDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+ uid)
        uDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("User fetch fail", p0.toString())
            }
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val fetchedUser : UserDetails = dataSnapshot!!.getValue(UserDetails::class.java)
                binding.contentProfile.profilesGenres.removeAllViews()
                fetchedUser.genres?.forEach { gen->
                    val tags : TextView = TextView(this@ProfileActivity)
                    tags.text = gen
                    tags.setTextColor(Color.parseColor("#00BEBA"))
                    tags.background = ContextCompat.getDrawable(this@ProfileActivity,R.drawable.textview_bg)
                    tags.textSize = 10f
                    val params = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(5, 5, 5, 5)

                    tags.layoutParams = params
                    binding.contentProfile.profilesGenres.addView(tags)
                }
                binding.contentProfile.editFollow.setOnClickListener {
                    val newBookDialog : DialogFragment = GenresSelectorFragment()
                    val args = Bundle()
                    args.putParcelable("user",fetchedUser)
                    newBookDialog.arguments = args
                    newBookDialog.show(supportFragmentManager,"fragment_genres_selector")
                }
                binding.setVariable(BR.user,fetchedUser)
                binding.contentProfile.setVariable(BR.user,fetchedUser)
                binding.executePendingBindings()

                if(Pref.fontChoice=="zawgyi") {
                    binding.contentProfile.profileLocation.text = Rabbit.uni2zg(fetchedUser.address)
                    if(fetchedUser.quote!=null)
                        binding.contentProfile.profileQuote.text = Rabbit.uni2zg(fetchedUser.quote)
                }
                if(Pref.fontChoice == "zawgyi")
                    binding.toolbarLayout.title = Rabbit.uni2zg(fetchedUser.displayName)
                else
                    binding.toolbarLayout.title = fetchedUser.displayName

                binding.contentProfile.profilePhone.setOnClickListener {
                    if(ContextCompat.checkSelfPermission(this@ProfileActivity,android.Manifest.permission.CALL_PHONE)
                            !=PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this@ProfileActivity,
                                arrayOf(android.Manifest.permission.CALL_PHONE),123)
                    }
                    else {
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel:" + fetchedUser.phone)
                        startActivity(intent)

                    }
                }

                Glide.with(this@ProfileActivity).load("http://graph.facebook.com/"+fetchedUser.fbID+"/picture?width=500")
                        .placeholder(Color.parseColor("#111111"))
                        .crossFade().into(binding.profileImage)
                Glide.with(this@ProfileActivity).load("http://graph.facebook.com/"+fetchedUser.fbID+"/picture?width=500")
                        .bitmapTransform(BlurTransformation(this@ProfileActivity))
                        .placeholder(Color.parseColor("#111111")).crossFade().into(binding.profileImageLarge)

            }

        })
    }
}
