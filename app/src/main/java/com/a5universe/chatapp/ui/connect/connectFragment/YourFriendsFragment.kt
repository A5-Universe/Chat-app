package com.a5universe.chatapp.ui.connect.connectFragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.a5universe.chatapp.R
import com.a5universe.chatapp.databinding.FragmentYourFriendsBinding
import com.a5universe.chatapp.model.connect.Friend
import com.a5universe.chatapp.model.connect.FriendRequest
import com.a5universe.chatapp.ui.chat.chatActivity.ChatActivity
import com.a5universe.chatapp.ui.connect.connectAdapter.YourFriendsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class YourFriendsFragment : Fragment(), YourFriendsAdapter.OnFriendsActionListener {
    lateinit var binding: FragmentYourFriendsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private lateinit var adapter: YourFriendsAdapter
    private val requestedUsers: ArrayList<FriendRequest> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentYourFriendsBinding.inflate(layoutInflater,container,false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("friends")
        val usersRef = database.getReference("user")

        // Initialize the adapter here
        adapter = YourFriendsAdapter(this, requestedUsers, this)

        binding.rcvYourFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvYourFriends.adapter = adapter

        // Fetch friend requests from Firebase Realtime Database
        fetchFriends(usersRef)

        return binding.root
    }

    // Inside your YourFriendsFragment class
    private fun fetchFriends(usersRef: DatabaseReference) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val friendsRef = database.getReference("friends")

        friendsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendsList: MutableList<FriendRequest> = mutableListOf()

                for (dataSnapshot in snapshot.children) {
                    val user1Id = dataSnapshot.child("user1Id").getValue(String::class.java)
                    val user2Id = dataSnapshot.child("user2Id").getValue(String::class.java)

                    // Check if the current user is involved in the friendship
                    if (user1Id == currentUserUid || user2Id == currentUserUid) {
                        // Exclude the current user's UID from the friends list
                        val friendUid = if (user1Id == currentUserUid) user2Id else user1Id

                        // Log the friendUid to see what data is being fetched
                        Log.d("FetchFriends", "Friend UID: $friendUid")

                        // Fetch friend's name and image from the users node
                        usersRef.child(friendUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val friendName = userSnapshot.child("name").getValue(String::class.java)
                                val friendImage = userSnapshot.child("imgUri").getValue(String::class.java)

                                // Log friend's name and image
                                Log.d("FetchFriends", "Friend Name: $friendName")
                                Log.d("FetchFriends", "Friend Image: $friendImage")

                                friendsList.add(
                                    FriendRequest(
                                        requestId = "",
                                        receiverUid = currentUserUid,
                                        senderUid = friendUid,
                                        senderName = friendName ?: "Unknown",
                                        senderImage = friendImage ?: "",
                                        status = "accepted"
                                    )
                                )

                                // Now, 'friendsList' contains the list of friends for the current user
                                // Move these lines outside the inner listener
                                requestedUsers.clear()
                                requestedUsers.addAll(friendsList)
                                adapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error if needed
                                Log.e("FetchFriends", "Error fetching friend details: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
                Log.e("FetchFriends", "Error fetching friends: ${error.message}")
            }
        })
    }

    override fun onChatFriend(user: FriendRequest) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("name", user.senderName ?: "Empty Name")
        intent.putExtra("ReceiverImage", user.senderImage ?: "")
        intent.putExtra("uid", user.senderUid ?: "")
        startActivity(intent)
    }

    //    test
    override fun onDeleteFriend(user: FriendRequest) {
    val dialog = Dialog(requireContext(), R.style.Dialogue)
    dialog.setContentView(R.layout.logout_dialogue_layout)

    val btnYes: TextView = dialog.findViewById(R.id.btnYes)
    val btnNo: TextView = dialog.findViewById(R.id.btnNo)

    btnYes.setOnClickListener {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (!currentUserUid.isNullOrBlank()) {
            val friendsRef = FirebaseDatabase.getInstance().getReference("friends")  // for friend node
            val friendRequestsRef = FirebaseDatabase.getInstance().getReference("friendRequests")   // for friend request node

            // for friend node delete code
            friendsRef.child(user.requestId ?: "").removeValue().addOnSuccessListener {
                val iterator = requestedUsers.iterator()
                while (iterator.hasNext()) {
                    val friendRequest = iterator.next()
                    if (friendRequest.requestId == user.requestId) {
                        iterator.remove()
                        adapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Friend deleted successfully!", Toast.LENGTH_SHORT).show()
                        break
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("DeleteFriend", "Error deleting friend: ${e.message}")
            }

            // for friend request node
            // Check if the current user's node exists
            friendRequestsRef.child(currentUserUid).child(user.requestId ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(currentUserSnapshot: DataSnapshot) {
                    if (currentUserSnapshot.exists()) {
                        // Current user's node exists, delete it
                        friendRequestsRef.child(currentUserUid).child(user.requestId ?: "").removeValue().addOnSuccessListener {
                            val iterator = requestedUsers.iterator()
                            while (iterator.hasNext()) {
                                val friendRequest = iterator.next()
                                if (friendRequest.requestId == user.requestId) {
                                    iterator.remove()
                                    adapter.notifyDataSetChanged()
                                    Toast.makeText(requireContext(), "Friend deleted successfully!", Toast.LENGTH_SHORT).show()
                                    break
                                }
                            }
                        }.addOnFailureListener { e ->
                            Log.e("DeleteFriend", "Error deleting friend request: ${e.message}")
                        }
                    } else {
                        // Current user's node does not exist, check friend's node
                        friendRequestsRef.child(user.senderUid ?: "").child(user.requestId ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(friendSnapshot: DataSnapshot) {
                                if (friendSnapshot.exists()) {
                                    // Friend's node exists, delete it
                                    friendRequestsRef.child(user.senderUid ?: "").child(user.requestId ?: "").removeValue().addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Friend deleted successfully!", Toast.LENGTH_SHORT).show()
                                        // Remove the friend from the local list
                                        val iterator = requestedUsers.iterator()
                                        while (iterator.hasNext()) {
                                            val friendRequest = iterator.next()
                                            if (friendRequest.requestId == user.requestId) {
                                                iterator.remove()
                                                adapter.notifyDataSetChanged()
                                                break
                                            }
                                        }
                                    }.addOnFailureListener { e ->
                                        Log.e("DeleteFriend", "Error deleting friend request: ${e.message}")
                                    }
                                } else {
                                    // Both nodes don't exist, handle accordingly
                                    Log.d("DeleteFriend", "Both nodes don't exist")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error if needed
                                Log.e("DeleteFriend", "Error checking friend request node: ${error.message}")
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                    Log.e("DeleteFriend", "Error checking friend request node: ${error.message}")
                }
            })
        }

        dialog.dismiss()
    }

    btnNo.setOnClickListener { dialog.dismiss() }
    dialog.show()
}


}






