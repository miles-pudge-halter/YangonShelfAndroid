package com.zwenex.ayoe.yangonshelf

import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.adroitandroid.chipcloud.ChipListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.databinding.ActivityGenresSelectorBinding

class GenresSelectorActivity : AppCompatActivity() {
    lateinit var binding: ActivityGenresSelectorBinding
    lateinit var genres: Array<String>
    lateinit var selectedGenres: ArrayList<String>
    lateinit var newUser : UserDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_genres_selector)
        val mainPd = ProgressDialog(this)
        mainPd.setMessage("Preparing the best for you")
        mainPd.show()
        selectedGenres = ArrayList<String>()
        newUser = intent.getParcelableExtra("newUser")
        FirebaseDatabase.getInstance().reference.child("genres")
                .addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(genSnap: DataSnapshot?) {
                        genres = Array<String>(genSnap?.childrenCount!!.toInt(),{i-> (genSnap.value as List<String>)[i]})
                        Log.e("GENRES:",genres.toString())
                        binding.genresChips.addChips(genres)
                        mainPd.hide()
                    }
                })
        binding.genresChips.setChipListener(object:ChipListener{
            override fun chipSelected(p0: Int) {
                selectedGenres.add(genres[p0])
            }
            override fun chipDeselected(p0: Int) {
                selectedGenres.remove(genres[p0])
            }
        })
        binding.submitGenres.setOnClickListener {
            if(selectedGenres.size<1) Log.e("Choose something","none selected")
            else{
                newUser.genres = selectedGenres
                val pd = ProgressDialog(this)
                pd.setMessage("Setting up user account")
                newUser.genres = selectedGenres
                Log.e("Finalized user:",newUser.toString())
                val userRef = FirebaseDatabase.getInstance().reference.child("users")
                val userHash = HashMap<String,Any>()
                userHash.put(FirebaseAuth.getInstance().currentUser!!.uid,newUser.toHashMap())
                pd.show()
                userRef.updateChildren(userHash).addOnCompleteListener {
                    pd.hide()
                    val intent = Intent(this,FontChoiceActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }
}
