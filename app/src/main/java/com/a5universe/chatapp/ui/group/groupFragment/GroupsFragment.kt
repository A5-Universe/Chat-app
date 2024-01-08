package com.a5universe.chatapp.ui.group.groupFragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.a5universe.chatapp.ui.group.groupAdapter.GroupAdapter
import com.a5universe.chatapp.databinding.FragmentGroupsBinding
import com.a5universe.chatapp.model.group.GroupDetails
import com.a5universe.chatapp.ui.group.groupActivity.GroupCreateActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var groupArrayList: ArrayList<GroupDetails>
    private lateinit var reference: DatabaseReference
    private lateinit var adapter: GroupAdapter
    private var currentUserUid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGroupsBinding.inflate(layoutInflater, container, false)

        // Firebase initialization
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("groups")
        groupArrayList = ArrayList()
        adapter = GroupAdapter(this, groupArrayList)

        binding.rcvGroupChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvGroupChat.adapter = adapter

        // Fetch groups from database code ....
        fetchGroupsFromDatabase()

        // Handle click on add group icon
        binding.addGroupIcon.setOnClickListener {
            // Handle click event, e.g., navigate to a new screen for adding a group
            startActivity(Intent(requireContext(), GroupCreateActivity::class.java))
        }

        return binding.root
    }

    private fun fetchGroupsFromDatabase() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupArrayList.clear()

                for (dataSnapshot in snapshot.children) {
                    val group = dataSnapshot.getValue(GroupDetails::class.java)
                    currentUserUid = auth.currentUser?.uid
                    if (group != null && group.members?.get(currentUserUid) == true) {
                        groupArrayList.add(group)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }




}