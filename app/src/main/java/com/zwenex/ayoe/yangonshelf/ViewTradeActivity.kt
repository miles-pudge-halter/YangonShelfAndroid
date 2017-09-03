package com.zwenex.ayoe.yangonshelf

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zwenex.ayoe.yangonshelf.Models.Book
import com.zwenex.ayoe.yangonshelf.Models.Notification
import com.zwenex.ayoe.yangonshelf.Models.Trade
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.Trade.TradeRecyclerViewAdapter
import com.zwenex.ayoe.yangonshelf.databinding.ActivityViewTradeBinding
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.rabbitconverter.rabbit.Rabbit
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ViewTradeActivity : AppCompatActivity() {
    lateinit var receivingAdapter : TradeRecyclerViewAdapter
    lateinit var givingAdapter : TradeRecyclerViewAdapter
    lateinit var binding : ActivityViewTradeBinding
    lateinit var receivingList : ArrayList<Book>
    lateinit var givingList : ArrayList<Book>
    lateinit var trade: Trade
    lateinit var uid:String
    lateinit var pd:ProgressDialog
    var dbRef = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_trade)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        pd= ProgressDialog(this)
        receivingList = ArrayList<Book>()
        givingList = ArrayList<Book>()
        receivingAdapter = TradeRecyclerViewAdapter(this, receivingList,"",null)
        givingAdapter = TradeRecyclerViewAdapter(this, givingList,"",null)
        val receivingRecycler = binding.receivingRecycler
        val givingRecycler = binding.givingRecycler
        val receivingLayoutManager : RecyclerView.LayoutManager = GridLayoutManager(this,2)
        val givingLayoutManager : RecyclerView.LayoutManager = GridLayoutManager(this,2)
        receivingRecycler.layoutManager = receivingLayoutManager
        givingRecycler.layoutManager = givingLayoutManager
        receivingRecycler.adapter = receivingAdapter
        givingRecycler.adapter = givingAdapter

        val passedTrade = intent.extras.getParcelable<Trade>("trade")
        getTrade(passedTrade.id!!)

    }

    fun getTrade(tradeId : String){
        dbRef.child("trade").child(tradeId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("Trade fetch faild, ", p0.toString())
            }
            override fun onDataChange(data : DataSnapshot?) {
                trade = data!!.getValue(Trade::class.java)
                trade.id = data.key
                dbRef.child("users").child(trade.requester).addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        Log.e("requester fetch failed",p0.toString())
                    }
                    override fun onDataChange(requesterSnap: DataSnapshot?) {
                        trade.requester_obj = requesterSnap!!.getValue(UserDetails::class.java)
                        dbRef.child("users").child(trade.requestee).addValueEventListener(object: ValueEventListener{
                            override fun onCancelled(p0: DatabaseError?) {
                                Log.e("requestee fetch failed",p0.toString())
                            }
                            override fun onDataChange(requesteeSnap: DataSnapshot?) {
                                trade.requestee_obj = requesteeSnap!!.getValue(UserDetails::class.java)
                                manageUi()
                            }
                        })
                    }
                })
            }

        })
    }
    fun manageUi(){
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        val status = binding.tradeStatus
        var giving = ArrayList<String>()
        var receiving = ArrayList<String>()
        if(trade.status == "denied") {
            binding.acceptDenyView.visibility = View.GONE
            status.text = getString(R.string.status_denied)
            status.background = resources.getDrawable(R.drawable.status_denied_bg)
        }
        else if(trade.status == "failed") {
            binding.acceptDenyView.visibility = View.GONE
            binding.confirmCancelView.visibility = View.GONE
            status.text = getString(R.string.status_failed)
            status.background = resources.getDrawable(R.drawable.status_denied_bg)
        }
        else if(trade.status == "accepted") {
            binding.acceptedMsg.visibility = View.VISIBLE
            binding.acceptDenyView.visibility = View.GONE
            binding.confirmCancelView.visibility = View.VISIBLE
            binding.tradeContactLayout.visibility = View.VISIBLE
            status.text = getString(R.string.status_accepted)
            status.background = resources.getDrawable(R.drawable.status_accepted_bg)
            Log.e("TRADE: ",trade.toString())
            if((uid == trade.requestee && trade.requesteeConfirm ==true) ||
                    (uid == trade.requester && trade.requesterConfirm == true)  ){
                binding.tradeConfirm.isEnabled = false
                binding.tradeCancel.isEnabled = false
                binding.acceptedMsg.append(Html.fromHtml("<br><b>You have confirmed the trade. Waiting for the other trader</b>."))
            }
        }
        else if(trade.status == "confirmed"){
            binding.confirmCancelView.visibility = View.GONE
            binding.acceptDenyView.visibility = View.GONE
            status.text = getString(R.string.status_confirmed)
            status.background = resources.getDrawable(R.drawable.status_confirmed_bg)
        }
        else{
            if(trade.requester == uid) {
                status.text = getString(R.string.status_pending_other_bg)
                binding.tradeAccept.isEnabled = false
                binding.tradeDeny.isEnabled = false
            }
            else {
                status.text = getString(R.string.status_pending)
                binding.tradeAccept.isEnabled = true
                binding.tradeDeny.isEnabled = true
            }
            status.background = resources.getDrawable(R.drawable.status_pending_bg)
        }

        if(trade.requester == uid){
            receiving = trade.giving!!
            giving = trade.requesting!!
            if(Pref.fontChoice=="zawgyi") {
                binding.otherGive.text = Rabbit.uni2zg("${trade.requestee_obj!!.displayName} give")
                binding.tradeContactPrefix.text = Rabbit.uni2zg(trade.requestee_obj!!.displayName)
            }
            else {
                binding.otherGive.text = "${trade.requestee_obj!!.displayName} give"
                binding.tradeContactPrefix.text = trade.requestee_obj!!.displayName
            }
            binding.tradeContact.text = trade.requestee_obj!!.phone
            Glide.with(this@ViewTradeActivity)
                    .load("http://graph.facebook.com/"+trade.requestee_obj?.fbID+"/picture?width=100")
                    .bitmapTransform(CropCircleTransformation(applicationContext))
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .crossFade().into(binding.otherProfile)
        }
        else{
            receiving = trade.requesting!!
            giving = trade.giving!!
            if(Pref.fontChoice=="zawgyi"){
                binding.otherGive.text = Rabbit.uni2zg("${trade.requester_obj!!.displayName} give")
                binding.tradeContactPrefix.text = Rabbit.uni2zg("${trade.requester_obj!!.displayName}: ")
            }
            else {
                binding.otherGive.text = "${trade.requester_obj!!.displayName} give"
                binding.tradeContactPrefix.text = "${trade.requester_obj!!.displayName}: "
            }
            binding.tradeContact.text = trade.requester_obj!!.phone
            Glide.with(applicationContext)
                    .load("http://graph.facebook.com/"+trade.requester_obj?.fbID+"/picture?width=100")
                    .bitmapTransform(CropCircleTransformation(applicationContext))
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .crossFade().into(binding.otherProfile)
        }
        binding.tradeContact.setOnClickListener { callPhone(uid) }
        binding.tradeAccept.setOnClickListener { acceptTrade() }
        binding.tradeDeny.setOnClickListener { denyTrade() }
        binding.tradeConfirm.setOnClickListener { confirmTrade() }
        binding.tradeCancel.setOnClickListener { cancelTrade() }
        getGivingBooks(giving)
        getReceivingBooks(receiving)
        getCurrentProfile(uid)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            123 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone(uid)
                } else {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
    fun callPhone(uid:String){
        var phone:String?=""
        if(uid == trade.requester) phone = trade.requestee_obj!!.phone
        else phone = trade.requester_obj!!.phone

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CALL_PHONE),123)
        }
        else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + phone)
            startActivity(intent)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    fun getCurrentProfile(uid:String){
        val userRef = dbRef.child("users").child(uid)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(UserDetails::class.java)
                Glide.with(applicationContext)
                        .load("http://graph.facebook.com/"+user.fbID+"/picture?width=100")
                        .bitmapTransform(CropCircleTransformation(applicationContext))
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .crossFade().into(binding.yourProfile)
            }
        })
    }
    fun getGivingBooks(books : ArrayList<String>){
        books.forEach { book ->
            val bookRef = dbRef.child("books").child(book)
            bookRef.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onDataChange(data: DataSnapshot) {
                    Log.e("GIVING: ", data.toString())
                    givingList.add(data.getValue(Book::class.java))
                    givingAdapter.notifyDataSetChanged()
                }

            })
        }
    }
    fun getReceivingBooks(books : ArrayList<String>){
        books.forEach { book ->
            val bookRef = dbRef.child("books").child(book)
            bookRef.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onDataChange(data: DataSnapshot) {
                    Log.e("RECEIVING:", data.toString())
                    receivingList.add(data.getValue(Book::class.java))
                    receivingAdapter.notifyDataSetChanged()
                }
            })
        }
    }
    fun acceptTrade(){
        pd.setMessage("Accepting trade")
        pd.show()
        val acceptRef = dbRef.child("trade").child(trade.id)
        val acceptNoti = dbRef.child("noti").child(trade.requester)
        val notiKey = acceptNoti.push().key
        acceptRef.child("status").setValue("accepted")
        acceptRef.child("requesterConfirm").setValue(false)
        acceptRef.child("requesteeConfirm").setValue(false)
        val timestamp = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss (zzzz)").format(Date())
        val noti = Notification(null,trade.requestee,null,trade.id,null,timestamp,"accept",false)
        val notiHash = HashMap<String, Any>()
        notiHash.put(notiKey,noti.toHashMap())
        acceptNoti.updateChildren(notiHash)
        for(book in trade.giving!!){
            dbRef
                    .child("books").child(book).child("pending").setValue(true)
        }
        for(book in trade.requesting!!){
            dbRef
                    .child("books").child(book).child("pending").setValue(true)
        }
        dbRef.child("trade")
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(data: DataSnapshot) {
                        var sendNoti = false
                        data.children.forEach { eachTrade ->
                            val trade1 = eachTrade.getValue(Trade::class.java)
                            if(eachTrade.key!=trade.id &&
                                    trade1.status!="confirmed"&&trade1.status!="failed"&&trade1.status!="denied"){
                                trade1.requesting!!.forEach { eachBook ->
                                    if(trade.requesting!!.contains(eachBook)){
                                        sendNoti = true
                                        Log.e("DUPLICATE REQUESTING___",eachBook)
                                        dbRef.child("trade")
                                                .child(eachTrade.key).child("status").setValue("failed")
                                    }
                                }
                                trade1.giving!!.forEach { eachBook ->
                                    if(trade.giving!!.contains(eachBook)){
                                        sendNoti = true
                                        Log.e("DUPLICATE GIVING___",eachBook)
                                        dbRef.child("trade")
                                                .child(eachTrade.key).child("status").setValue("failed")
                                    }
                                }
                                if(sendNoti){
                                    val nID1 = dbRef.child("noti").child(trade1.requester).push().key
                                    val nID2 = dbRef.child("noti").child(trade1.requestee).push().key
                                    val nTimestamp = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss (zzzz)").format(Date())
                                    val noti1 = Notification(null,trade1.requestee,null,eachTrade.key,null,nTimestamp,"deny",false)
                                    val noti1Hash = HashMap<String,Any>()
                                    noti1Hash.put(nID1,noti1)
                                    val noti2 = Notification(null,trade1.requester,null,eachTrade.key,null,nTimestamp,"deny",false)
                                    val noti2Hash = HashMap<String,Any>()
                                    noti2Hash.put(nID2,noti2)
                                    dbRef.child("noti").child(trade1.requester).updateChildren(noti1Hash).addOnCompleteListener {
                                        dbRef.child("noti").child(trade1.requestee).updateChildren(noti2Hash)
                                    }
                                }
                            }
                        }

                        pd.hide()
                        onBackPressed()
                        finish()
                    }
                })
    }
    fun denyTrade(){
        pd.setMessage("Denying trade")
        pd.show()
        val notiKey = dbRef.child("noti/"+trade.requester).push().key
        val timestamp = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss (zzzz)").format(Date())
        dbRef.child("trade/"+trade.id).child("status").setValue("denied").addOnCompleteListener {
            val noti = Notification(null,trade.requestee,null,trade.id,null,timestamp,"deny",false)
            val notiHash = HashMap<String, Any>()
            notiHash.put(notiKey,noti.toHashMap())
            dbRef.child("noti/"+trade.requester).updateChildren(notiHash).addOnCompleteListener {
                pd.hide()
                onBackPressed()
                finish()
            }
        }
    }
    fun confirmTrade(){
        Log.e("TRADE ID FOR CONFIRM:",trade.id)
        pd.setMessage("Confirming trade")
        pd.show()
        Log.e("TRADING CONFIRM:: ",trade.toString())
        val confirmRef = dbRef.child("trade").child(trade.id)
        if(uid == trade.requester)
            confirmRef.child("requesterConfirm").setValue(true).addOnCompleteListener {
                bothConfirm(confirmRef)
            }
        else
            confirmRef.child("requesteeConfirm").setValue(true).addOnCompleteListener {
                bothConfirm(confirmRef)
            }

    }
    fun cancelTrade(){
        pd.setMessage("Cancelling trade")
        pd.show()
        val tradeRef = dbRef.child("trade").child(trade.id)
        tradeRef.child("status")
                .setValue("failed").addOnCompleteListener {
            if(uid == trade.requestee)
                tradeRef.child("requesteeFail").setValue(true)
            else
                tradeRef.child("requesterFail").setValue(true)
            val bookRef = dbRef.child("books")
            trade.requesting?.forEach { book->
                bookRef.child(book).child("pending").setValue(false).addOnCompleteListener {
                    pd.hide()
                    onBackPressed()
                    finish()
                }
            }
            trade.giving?.forEach { book->
                bookRef.child(book).child("pending").setValue(false).addOnCompleteListener {
                    pd.hide()
                    onBackPressed()
                    finish()
                }
            }
        }
    }
    fun bothConfirm(confirmRef:DatabaseReference){
        confirmRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}
            override fun onDataChange(snap: DataSnapshot) {
                val tradeSnap = snap.getValue(Trade::class.java)
                Log.e("SNAPSHOT:: ",tradeSnap.toString())
                if(tradeSnap.requesteeConfirm==true && tradeSnap.requesterConfirm==true) {
                    confirmRef.child("status").setValue("confirmed")
                    tradeSnap.requesting!!.forEach { book->
                        Log.e("CHANGING HOLDER1",trade.requester)
                        dbRef.child("books").child(book).child("currentHolder").setValue(tradeSnap.requester).addOnCompleteListener {
                            dbRef.child("books").child(book).child("pending").setValue(false)
                        }
                    }
                    tradeSnap.giving!!.forEach { book2->
                        Log.e("CHANGING HOLDER2",tradeSnap.requestee)
                        dbRef.child("books").child(book2).child("currentHolder").setValue(tradeSnap.requestee).addOnCompleteListener {
                            dbRef.child("books").child(book2).child("pending").setValue(false)
                        }
                    }


                }else Log.e("Trade status ___","STILL PENDING")
                pd.hide()
                onBackPressed()
            }
        })
    }
}
