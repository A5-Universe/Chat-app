package com.a5universe.chatapp.ui.meet

import  android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.a5universe.chatapp.databinding.FragmentHomeMeetBinding
import com.a5universe.chatapp.model.authentication.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import java.net.MalformedURLException
import java.net.URL



class HomeMeetFragment : Fragment() {

    private lateinit var binding: FragmentHomeMeetBinding
    private lateinit var roomName: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersArrayList: ArrayList<User>
    private lateinit var currentUser: User
    private lateinit var email: String
    private lateinit var name: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeMeetBinding.inflate(layoutInflater,container,false)
//
//        //firebase getInstance
//        auth = FirebaseAuth.getInstance()
//        database = FirebaseDatabase.getInstance()
//        usersArrayList = ArrayList()
//
//        val reference: DatabaseReference = database.reference.child("user").child(auth.uid!!)
//
//        reference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                //get data
//                email = snapshot.child("email").value.toString()
//                name = snapshot.child("name").value.toString()
//
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Handle the error
//                Log.e("FirebaseError","Error occured: ${error.message}")
//            }
//        })
//
//
//
//        binding.btnJoin.setOnClickListener {
//            roomName =  binding.roomCode.text.toString()
//
//            if (roomName.isEmpty()) {
//                Toast.makeText(requireContext(), "Please enter room id", Toast.LENGTH_SHORT).show()
//            } else {
//                // meet joining code..
////                joinMeeting(roomName)
//                Log.d("TAG", "Join button clicked for room: $roomName")
//            }
//        }
//
//        binding.btnCreate.setOnClickListener {
//            roomName =  binding.roomCode.text.toString()
//
//            if (roomName.isEmpty()) {
//                Toast.makeText(requireContext(), "Please enter room id", Toast.LENGTH_SHORT).show()
//            } else {
//                // create meet code..
//                createMeeting(roomName, name, email)
//                Log.d("TAG", "Create button clicked for room: $roomName")
//            }
//
//        }
//
//        binding.btnShare.setOnClickListener {
//            val string = binding.roomCode.text.toString()
//            val intent = Intent()
//            intent.action = Intent.ACTION_SEND
//            intent.putExtra(Intent.EXTRA_TEXT, string)
//            intent.type = "text/plain"
//            startActivity(intent)
//        }

        return binding.root

    }


//    private fun joinMeeting(roomName: String) {
//        try {
//            val serverURL = URL("https://meet.jit.si")
//            val options = JitsiMeetConferenceOptions.Builder()
//                .setServerURL(serverURL)
//                .setRoom(roomName)
//                .build()
//
//            JitsiMeetActivity.launch(requireContext(), options)
//        } catch (e: MalformedURLException) {
//            // Handle the exception, e.g., show an error message
//            Toast.makeText(requireContext(), "Invalid server URL", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun createMeeting(roomName: String, name: String, email: String) {
//
//        try {
//            val serverURL = URL("https://meet.jit.si")
//
//            val optionsBuilder = JitsiMeetConferenceOptions.Builder()
//                .setServerURL(serverURL)
//                .setRoom(roomName)
//                .setFeatureFlag("invite.enabled", false)
//                .setFeatureFlag("meeting-name.enabled", false)
//                .setFeatureFlag("live-streaming.enabled", false)
//                .setFeatureFlag("recording.enabled", false)
//                .setFeatureFlag("tile-view.enabled", false)
//                .setFeatureFlag("chat.enabled", false)
//                .setFeatureFlag("calendar.enabled", false)
//                .setUserInfo(JitsiMeetUserInfo().apply {
//                    this.displayName = name
//                    this.email = email
//                })
//
//            // Check if the user's email makes them a moderator
//            val isModerator = isModerator(email)
//
//            if (isModerator) {
//                // Set moderator-specific feature flags or perform actions
//                optionsBuilder.setFeatureFlag("start-locked.enabled", true)
//                // Add more feature flags or actions for moderators
//            }
//            val options = optionsBuilder.build()
//
//            JitsiMeetActivity.launch(requireContext(), options)
//
//        } catch (e: MalformedURLException) {
//            // Handle the exception, e.g., show an error message
//            Toast.makeText(requireContext(), "Invalid server URL", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Function to determine if the user is a moderator based on email
//    private fun isModerator(userEmail: String): Boolean {
//        // Implement your logic to determine if the user is a moderator
//        // For example, check if the email matches a list of moderator emails
//        return userEmail == email // Replace with your logic
//    }


}