package com.a5universe.chatapp.ui.home


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.a5universe.chatapp.R
import com.a5universe.chatapp.databinding.ActivityHomeBinding
import com.a5universe.chatapp.model.authentication.User
import com.a5universe.chatapp.ui.authentication.login.LoginActivity
import com.a5universe.chatapp.ui.authentication.profile.ProfileActivity
import com.a5universe.chatapp.ui.chat.chatFragment.HomeChatFragment
import com.a5universe.chatapp.ui.connect.connectFragment.HomeConnectFragment
import com.a5universe.chatapp.ui.meet.HomeMeetFragment
import com.a5universe.chatapp.ui.news.newsFragment.HomeNewsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersArrayList: ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase getInstance
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        // Initialize usersArrayList
        usersArrayList = ArrayList()


        // If user not login go to login activity
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Finish the current activity to prevent coming back to it after logging out
        }

        // toolbar and drawer layout
        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //home chat fragment loaded
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeChatFragment()
            ).commit()
            navigationView.setCheckedItem(R.id.nav_chat)
        }


        // for nav_header layout item
        val headerView = navigationView.inflateHeaderView(R.layout.nav_header)
        val btnBack = headerView.findViewById<ImageView>(R.id.btnBack)
        val viewProfile = headerView.findViewById<TextView>(R.id.viewProfile)
        val profileImg = headerView.findViewById<ImageView>(R.id.userImg)
        val profileName = headerView.findViewById<TextView>(R.id.userName)


        // Set a click listener for the back button
        btnBack.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        //fetch data firebase
        val reference: DatabaseReference = database.reference.child("user").child(auth.uid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val name = snapshot.child("name").value.toString()
                val image = snapshot.child("imgUri").value.toString()

                profileName.text = name
                val placeholderImageUrl = "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"

                if (image.isEmpty()) {
                    Picasso.get().load(placeholderImageUrl).into(profileImg)
                    Picasso.get().load(placeholderImageUrl).into(binding.userImage)
                } else {
                    Picasso.get().load(image).into(profileImg)
                    Picasso.get().load(image).into(binding.userImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        //  Set a click listener for the view profile button
        viewProfile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
            drawerLayout.closeDrawer(GravityCompat.START)
        }


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        when(item.itemId){
            //nav_menu item
            R.id.nav_chat ->{
                if (currentFragment !is HomeChatFragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeChatFragment())
                        .commit()
                }
            }
            R.id.nav_connect -> {
                if (currentFragment !is HomeConnectFragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeConnectFragment())
                        .commit()
                }
            }
            R.id.nav_news -> {
                if (currentFragment !is HomeNewsFragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeNewsFragment())
                        .commit()
                }
            }
            R.id.nav_meet -> {
//                if (currentFragment !is HomeMeetFragment) {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, HomeMeetFragment())
//                        .commit()
//                }
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_setting -> {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                val dialog = Dialog(this, R.style.Dialogue)
                dialog.setContentView(R.layout.logout_dialogue_layout)

                val btnYes: TextView = dialog.findViewById(R.id.btnYes)
                val btnNo: TextView = dialog.findViewById(R.id.btnNo)

                btnYes.setOnClickListener {

                    //when user logout show offline
                    updateUserStatus("offline")

                    FirebaseAuth.getInstance().signOut()
                    // Update user status to "offline" before logging out

                    startActivity(Intent(this, LoginActivity::class.java))
                    dialog.dismiss()
                    finish() // Finish the current activity to prevent coming back to it after logging out
                }
                btnNo.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
           onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        // Set user status to online when activity is resumed
        updateUserStatus("online")
    }

    override fun onPause() {
        super.onPause()
        // Set user status to offline when activity is paused
        updateUserStatus("offline")
    }

    private fun updateUserStatus(status: String) {
        database.reference.child("user").child(auth.uid!!).child("status").setValue(status)
    }

}