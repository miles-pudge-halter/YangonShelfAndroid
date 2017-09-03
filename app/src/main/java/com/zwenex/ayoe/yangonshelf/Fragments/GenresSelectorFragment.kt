package com.zwenex.ayoe.yangonshelf.Fragments
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.adroitandroid.chipcloud.ChipListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.R
import com.zwenex.ayoe.yangonshelf.databinding.FragmentGenresSelectorBinding

/**
 * A simple [Fragment] subclass.
 */
class GenresSelectorFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    lateinit var binding : FragmentGenresSelectorBinding
    lateinit var genres: Array<String>
    lateinit var selectedGenres: ArrayList<String>
    lateinit var user: UserDetails
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_genres_selector,container,false)
        binding = DataBindingUtil.bind(view)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        val bundle = arguments
        user = bundle.getParcelable<UserDetails>("user")
        retainInstance = true
        selectedGenres = ArrayList<String>()
        FirebaseDatabase.getInstance().reference.child("genres")
                .addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {}
                    override fun onDataChange(genSnap: DataSnapshot?) {
                        genres = Array(genSnap?.childrenCount!!.toInt(),{ i-> (genSnap.value as List<String>)[i]})
                        Log.e("GENRES:",genres.toString())
                        binding.genresChips.addChips(genres)
                        binding.loading.visibility = View.GONE
                        user.genres?.forEach { uGenres->
                            binding.genresChips.setSelectedChip(genres.indexOf(uGenres))
                        }
                    }
                })
        binding.genresChips.setChipListener(object: ChipListener {
            override fun chipSelected(p0: Int) {
                selectedGenres.add(genres[p0])
            }
            override fun chipDeselected(p0: Int) {
                selectedGenres.remove(genres[p0])
            }
        })
        binding.updateGenres.setOnClickListener {
            if(selectedGenres.size<1){
                Toast.makeText(context,"You must select at least one genre",Toast.LENGTH_SHORT).show()
            }
            else {
                binding.genresChips.visibility = View.GONE
                binding.loading.visibility = View.VISIBLE
                Log.e("SELECTED:", selectedGenres.toString())
                val mDatabase = FirebaseDatabase.getInstance().reference.child("users")
                        .child(FirebaseAuth.getInstance().currentUser?.uid)
                mDatabase.child("genres").setValue(selectedGenres).addOnCompleteListener {
                    binding.loading.visibility = View.GONE
                    dismiss()
                }
            }
        }
        return view
    }

}// Required empty public constructor


