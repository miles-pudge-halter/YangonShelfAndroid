package com.zwenex.ayoe.yangonshelf

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.zwenex.ayoe.yangonshelf.databinding.ActivityFontChoiceBinding

class FontChoiceActivity : AppCompatActivity() {

    lateinit var binding : ActivityFontChoiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_font_choice)
        binding.submitBtn.setOnClickListener {
            Log.e("fab","clicked")
            if(binding.radioZawgyi.isChecked){
                Pref.fontChoice = "zawgyi"
                val intent = Intent(this, FeedNavActivity::class.java)
                startActivity(intent)
                finish()
            }
            else if(binding.radioUnicode.isChecked){
                Pref.fontChoice = "unicode"
                val intent = Intent(this, FeedNavActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Snackbar.make(binding.relLayout,"You must choose one.",Snackbar.LENGTH_SHORT)
            }
        }
    }
}
