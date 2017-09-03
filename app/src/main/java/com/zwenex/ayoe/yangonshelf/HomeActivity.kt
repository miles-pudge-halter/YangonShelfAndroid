package com.zwenex.ayoe.yangonshelf

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.zwenex.ayoe.yangonshelf.Fragments.FeedFragment
import com.zwenex.ayoe.yangonshelf.Models.UserDetails
import com.zwenex.ayoe.yangonshelf.Profile.ProfileActivity
import jp.wasabeef.glide.transformations.CropCircleTransformation

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var searchView : MaterialSearchView? = null
    var navView : NavigationView? = null
    var navName : TextView? = null
    var navEmail : TextView? = null
    var navProfile : ImageView? = null
    lateinit var fm : FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        val toolbarTitle = (toolbar.findViewById(R.id.toolbar_title) as TextView)
        toolbarTitle.textSize = 26f
        toolbarTitle.typeface = Typeface.createFromAsset(this.assets,"font/PoiretOne-Regular.ttf")
        setSupportActionBar(toolbar)

        fm  = supportFragmentManager

        searchView = findViewById(R.id.search_view) as MaterialSearchView


        searchView?.setVoiceSearch(true)
        searchView?.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                var intent = Intent(this@HomeActivity, SearchActivity::class.java)
                intent.putExtra("query",query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //Do some magic
                return false
            }
        })

        searchView?.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
            }

            override fun onSearchViewClosed() {
                //Do some magic
            }
        })

        navView = findViewById(R.id.nav_view) as NavigationView
        var headerView : View = navView!!.getHeaderView(0)
        navName = headerView.findViewById(R.id.navName) as TextView
        navProfile = headerView.findViewById(R.id.navProfile) as ImageView
        navEmail = headerView.findViewById(R.id.navEmail) as TextView

        navProfile!!.setOnClickListener {
            var intent: Intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("uid",FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(intent)
        }

        var genresRef = FirebaseDatabase.getInstance().getReference("genres")
        genresRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) { Log.e("genres fetch fail:",p0.toString()) }

            override fun onDataChange(snapshot: DataSnapshot?) {
                val genres :List<String> = snapshot!!.value as List<String>
                searchView!!.setSuggestions(genres.toTypedArray())
            }

        })

        onNavigationItemSelected(navView!!.menu.getItem(0))

//        val fab = findViewById(R.id.fab) as FloatingActionButton
//        fab.setOnClickListener(View.OnClickListener { view ->
//            val newBookDialog : DialogFragment = AddBookDialogFragment()
//            newBookDialog.show(supportFragmentManager,"fragment_add_book_dialog")
//        })

        updateNavHeader()
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }


    fun updateNavHeader(){
        //var navView : NavigationView = findViewById(R.id.nav_view) as NavigationView

        val mDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().currentUser!!.uid)
        mDatabase.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.e("user fetch fail at nav",p0.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot?) {
                val user : UserDetails = snapshot!!.getValue(UserDetails::class.java)
                navName!!.setText(user.displayName)
                navEmail!!.setText(user.email)
                Glide.with(applicationContext).load("http://graph.facebook.com/"+user.fbID+"/picture?type=large").bitmapTransform(CropCircleTransformation(applicationContext)).into(navProfile)
            }

        })

    }

    override fun onBackPressed() {

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (searchView!!.isSearchOpen()) {
            searchView?.closeSearch();
        }
        else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)

        val item = menu.findItem(R.id.action_search)
        searchView?.setMenuItem(item)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_feed && fm.findFragmentByTag("feed")==null) {
            Log.e("Made a new fragment","feed")
            fm.beginTransaction().replace(R.id.frame_content,  FeedFragment(),"feed").commit()
            // Handle the camera action
        } else if (id == R.id.nav_trade) {
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder(this)
                    .setTitle("Log out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", { dialog, which ->
                        FirebaseAuth.getInstance().signOut()
                        LoginManager.getInstance().logOut()
                        var intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    })
                    .setNegativeButton("No",null).show()
        }
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
