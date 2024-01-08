package com.a5universe.chatapp.ui.connect.connectFragment

import RequestFriendsAdapter
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.a5universe.chatapp.databinding.FragmentRequestFriendsBinding
import com.a5universe.chatapp.model.connect.Friend
import com.a5universe.chatapp.model.connect.FriendRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestFriendsFragment : Fragment(), RequestFriendsAdapter.OnRequestActionListener  {

    private lateinit var binding: FragmentRequestFriendsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: RequestFriendsAdapter
    private val requestedUsers: ArrayList<FriendRequest> = ArrayList()

//test companion object


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestFriendsBinding.inflate(layoutInflater,container,false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        adapter = RequestFriendsAdapter(this, requestedUsers, this)
        binding.rcvRequestFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvRequestFriends.adapter = adapter

        fetchFriendRequests()

        return binding.root
    }



    private fun fetchFriendRequests() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        val friendRequestsRef = database.getReference("friendRequests").child(currentUserUid!!)

        friendRequestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendRequests: MutableList<FriendRequest> = mutableListOf()

                for (dataSnapshot in snapshot.children) {
                    val requestId = dataSnapshot.key
                    val receiverUid = dataSnapshot.child("receiverUid").getValue(String::class.java)
                    val senderUid = dataSnapshot.child("senderUid").getValue(String::class.java)
                    val senderName = dataSnapshot.child("senderName").getValue(String::class.java)
                    val senderImage = dataSnapshot.child("senderImage").getValue(String::class.java)
                    val status = dataSnapshot.child("status").getValue(String::class.java)

                    // Check if the status is not "accepted" before adding to the list
                    if (status == "pending") {
                        val friendRequest = FriendRequest(requestId, receiverUid, senderUid, senderName, senderImage, status)
                        friendRequests.add(friendRequest)
                    }
                }

                requestedUsers.clear()
                requestedUsers.addAll(friendRequests)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    override fun onAcceptFriendRequest(user: FriendRequest) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val friendRequestsRef = database.getReference("friendRequests").child(currentUserUid!!).child(user.requestId!!)

        // Update the status to "accepted" in friend_requests
        friendRequestsRef.child("status").setValue("accepted")
            .addOnSuccessListener {
                // Friend request accepted successfully

                // Create a new entry in the "friends" node
                val friendsRef = database.getReference("friends")
                val friendshipId = friendsRef.push().key // Generate a unique key for the friendship
                val friend = Friend(friendshipId!!, currentUserUid, user.senderUid!!)
                friendsRef.child(friendshipId).setValue(friend)

                Toast.makeText(requireContext(), "Friend request accepted!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to accept friend request", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDeclineFriendRequest(user: FriendRequest) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val friendRequestsRef = database.getReference("friendRequests").child(currentUserUid!!).child(user.requestId!!)

        // Remove the friend request from friend_requests
        friendRequestsRef.removeValue()
            .addOnSuccessListener {
                // Friend request removed successfully
                // Remove the friend request from the local list
                requestedUsers.remove(user)
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Friend request declined!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to decline friend request", Toast.LENGTH_SHORT).show()
            }
    }

}
