package com.a5universe.chatapp.ui.chat.chatFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.a5universe.chatapp.ui.chat.chatAdapter.UserChatAdapter
import com.a5universe.chatapp.databinding.FragmentChatBinding
import com.a5universe.chatapp.model.connect.FriendRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var friendRequestsList: ArrayList<FriendRequest>
    private lateinit var reference: DatabaseReference
    private lateinit var adapter: UserChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChatBinding.inflate(layoutInflater,container,false)

        //firebase initialization
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        reference = database.getReference("friends")
        val usersRef = database.getReference("user")
        friendRequestsList = ArrayList()
        adapter = UserChatAdapter(this, friendRequestsList)


        //test
        // Fetch friends to filter users in the ChatFragment
        fetchFriendsForChat(usersRef)
        //test


        binding.rcvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvChat.adapter = adapter

        return binding.root
    }

//    test
    private fun fetchFriendsForChat(usersRef: DatabaseReference) {
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
//                        Log.d("FetchFriends", "Friend UID: $friendUid")

                        // Fetch friend's name and image from the users node
                        usersRef.child(friendUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val friendName = userSnapshot.child("name").getValue(String::class.java)
                                val friendImage = userSnapshot.child("imgUri").getValue(String::class.java)

                                // Log friend's name and image
//                                Log.d("FetchFriends", "Friend Name: $friendName")
//                                Log.d("FetchFriends", "Friend Image: $friendImage")

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
                                friendRequestsList.clear()
                                friendRequestsList.addAll(friendsList)
                                adapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error if needed
//                                Log.e("FetchFriends", "Error fetching friend details: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
//                Log.e("FetchFriends", "Error fetching friends: ${error.message}")
            }
        })
    }
//    test


}