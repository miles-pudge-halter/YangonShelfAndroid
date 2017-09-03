package com.zwenex.ayoe.yangonshelf

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.Trade
import com.zwenex.ayoe.yangonshelf.Models.UserDetails


class MainActivity : AppCompatActivity() {

    private var mAuthListener:FirebaseAuth.AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null
    lateinit var loginButton : LoginButton
    lateinit var progressDialog : ProgressDialog
    var mCallbackManager : CallbackManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.extras != null) {
            if(intent.extras.getString("tradeID")!=null) {
                val trade = Trade()
                trade.id = intent.extras.getString("tradeID")
                val intent = Intent(this, ViewTradeActivity::class.java)
                intent.putExtra("trade", trade)
                startActivity(intent)
                finish()
            }
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging in")

        mCallbackManager = CallbackManager.Factory.create()
        loginButton = findViewById(R.id.login_button) as LoginButton
        loginButton.setReadPermissions("email","public_profile")

        loginButton.setOnClickListener {
            progressDialog.show()
        }

        mAuth = FirebaseAuth.getInstance()

        loginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("bay bay ka lone", "facebook:onSuccess:" + loginResult)
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("bay bay ka lone", "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d("bay bay ka lone", "facebook:onError", error)
                // ...
            }
        })

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.currentUser
        Log.e("CURRENT USER:", FirebaseAuth.getInstance().currentUser.toString())
        currentUser?.let{updateUI(FirebaseAuth.getInstance().currentUser)}
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("bay bay ka lone", "handleFacebookAccessToken:" + token)
        // [START_EXCLUDE silent]
        //showProgressDialog()
        // [END_EXCLUDE]

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("bay bay ka lone", "signInWithCredential:success")
                        updateUI(FirebaseAuth.getInstance().currentUser!!)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("bay bay ka lone", "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@MainActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }

                    // [START_EXCLUDE]
                    //hideProgressDialog()
                    // [END_EXCLUDE]
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
    }
    fun updateUI(user:FirebaseUser?){
        if(!Pref.isRegistered) {
            Log.e("USER IS REGISTERED:",Pref.isRegistered.toString())
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(user?.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onDataChange(data: DataSnapshot?) {
                    if(data?.value!=null){
                        val iidHash = HashMap<String,Any?>()
                        iidHash.put("instanceID",Pref.instanceId)
                        FirebaseDatabase.getInstance().reference.child("users").child(user?.uid).updateChildren(iidHash).addOnCompleteListener {
                            progressDialog.hide()
                            Log.e("LOGGED IN DATA:",data.toString())
                            Pref.isRegistered = true
                            changeActivity()
                            finish()
                        }

                    }
                    else{
                        progressDialog.hide()
                        val intent = Intent(applicationContext, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }
        else {
            changeActivity()
        }
    }
    fun changeActivity(){
        if(Pref.fontChoice.isNullOrEmpty()) {
            val intent = Intent(applicationContext, FontChoiceActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent = Intent(applicationContext, FeedNavActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}

