package com.a5universe.chatapp.Activity


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.a5universe.chatapp.Fragment.ChatFragment
import com.a5universe.chatapp.Model.Users
import com.a5universe.chatapp.R
import com.a5universe.chatapp.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    lateinit var binding: ActivityHomeBinding
    lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase getInstance
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // If user not login go to login activity
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // toolbar and drawerlayout
        setSupportActionBar(binding.toolbar)
        drawerLayout = binding.drawerLayout

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //chat fragment loaded
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,ChatFragment()).commit()
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
                Picasso.get().load(image).into(profileImg)
                Picasso.get().load(image).into(binding.userImage)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })

        //  Set a click listener for the view profile button
        viewProfile.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
//            nav_menu item
            R.id.nav_chat -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ChatFragment()).commit()
            R.id.nav_groups -> startActivity(Intent(this, GroupActivity::class.java))
            R.id.nav_meet -> startActivity(Intent(this, MeetingActivity::class.java))
            R.id.nav_setting -> Toast.makeText(this, "Setting is coming soon", Toast.LENGTH_SHORT).show()
            R.id.nav_logout -> {
                val dialog = Dialog(this, R.style.Dialogue)
                dialog.setContentView(R.layout.logout_dialogue_layout)

                val btnYes: TextView = dialog.findViewById(R.id.btnYes)
                val btnNo: TextView = dialog.findViewById(R.id.btnNo)

                btnYes.setOnClickListener {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    dialog.dismiss()
                }
                btnNo.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
           onBackPressedDispatcher.onBackPressed()
        }
    }

}