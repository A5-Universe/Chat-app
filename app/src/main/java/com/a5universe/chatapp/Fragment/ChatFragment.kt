package com.a5universe.chatapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.a5universe.chatapp.Adapter.UserAdapter
import com.a5universe.chatapp.Model.Users
import com.a5universe.chatapp.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    private lateinit var usersArrayList: ArrayList<Users>
    private lateinit var adapter: UserAdapter
    private lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatBinding.inflate(layoutInflater,container,false)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        reference = database.getReference("user")
        usersArrayList = ArrayList()
        adapter = UserAdapter(this, usersArrayList)


        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList.clear()
                for (dataSnapshot in snapshot.children) {
                    val users = dataSnapshot.getValue(Users::class.java)
                    if (users != null) {
                        usersArrayList.add(users)
                    } else{
                        Toast.makeText(requireContext(), "Failed to add arraylist", Toast.LENGTH_SHORT).show()
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.rcvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvChat.adapter = adapter

        return binding.root
    }

}