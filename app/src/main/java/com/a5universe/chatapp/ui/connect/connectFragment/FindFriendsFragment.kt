    package com.a5universe.chatapp.ui.connect.connectFragment

    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.SearchView
    import android.widget.Toast
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.a5universe.chatapp.model.connect.FriendRequest
    import com.a5universe.chatapp.model.authentication.User
    import com.a5universe.chatapp.databinding.FragmentFindFriendsBinding
    import com.a5universe.chatapp.ui.connect.connectAdapter.FindFriendsAdapter
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener


    class FindFriendsFragment : Fragment(), FindFriendsAdapter.OnAddFriendClickListener {

        private lateinit var binding: FragmentFindFriendsBinding
        private lateinit var auth: FirebaseAuth
        private lateinit var database: FirebaseDatabase
        private lateinit var usersArrayList: ArrayList<User>
        private lateinit var reference: DatabaseReference
        private lateinit var adapter: FindFriendsAdapter


//        test

//        test

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            binding = FragmentFindFriendsBinding.inflate(layoutInflater,container,false)

            //firebase initialization
            auth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("user")
            usersArrayList = ArrayList()
            adapter = FindFriendsAdapter(requireContext(), usersArrayList, this)


            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Ensure the fragment is still attached to a context
                    if (!isAdded || context == null) {
                        return
                    }

                    usersArrayList.clear()
                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                    for (dataSnapshot in snapshot.children) {
                        val users = dataSnapshot.getValue(User::class.java)
                        if (users != null && users.uid != currentUserUid ) {
                            usersArrayList.add(users)
                        }
                      else{
    //                        Toast.makeText(requireContext(), "Failed to add list", Toast.LENGTH_SHORT).show()
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            })

            binding.rcvFindFriends.layoutManager = LinearLayoutManager(requireContext())
            binding.rcvFindFriends.adapter = adapter


            // Initialize search functionality
            searchFunctionality()                //test


            return binding.root
        }




        //    test

        private fun searchFunctionality() {
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Update the adapter with the filtered list based on the search query
                    newText?.let { adapter.filter(it) }
                    return true
                }
            })
        }
    //    test


         override fun onAddFriendClick(user: User) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid // current user id
        val recipientUid = user.uid // friend user id

        // Reference to the current user's friend_requests node
        val currentUserFriendRequestsRef = FirebaseDatabase.getInstance().getReference("friendRequests").child(currentUserUid!!)

        // Reference to the recipient user's friend_requests node
        val recipientFriendRequestsRef = FirebaseDatabase.getInstance().getReference("friendRequests").child(recipientUid!!)

        // Check if the current user has already sent a friend request to the recipient
        currentUserFriendRequestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(currentUserSnapshot: DataSnapshot) {

                //when current user child exist
                if (currentUserSnapshot.exists()) {

                    //check for senderUid == friend userid
                    var senderUidFound = false
                    for (requestSnapshot in currentUserSnapshot.children) {
                        val requestSenderUid = requestSnapshot.child("senderUid").getValue(String::class.java)
                        if (requestSenderUid == recipientUid) {
                            senderUidFound = true
                            break
                        }
                    }
                    //check for senderUid == friend userid
                    if (senderUidFound) {
                        // Handle the case when senderUidToCheck exists for recipientUid
                        // For example:
                        // showToast("SenderUid exists for recipientUid")
                        Toast.makeText(requireContext(), "Your Friend is already sent it you.", Toast.LENGTH_SHORT).show()
    //                    Log.d("TAG", "sender uid is present : $senderUidFound ")
                    } else {
                        // logic from creating friend request.... method creating for this logic
                        friendRequestSending(currentUserUid, recipientFriendRequestsRef, user)  // testing ..
    //                    Log.d("TAG", " Request sending.......... ")
                    }

    //                when friend user child exist
                } else {
                    // The child node with currentUserUid does not exist
                    // Now, check if the child node with recipientUid exists under recipientFriendRequestsRef
                    recipientFriendRequestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(recipientSnapshot: DataSnapshot) {
                            if (recipientSnapshot.exists()) {

                                //check for senderUid == current userid
                                var senderUidFound = false
                                for (requestSnapshot in recipientSnapshot.children) {
                                    val requestSenderUid = requestSnapshot.child("senderUid").getValue(String::class.java)
                                    if (requestSenderUid == currentUserUid) {
                                        senderUidFound = true
                                        break
                                    }
                                }
                                //end check for senderUid == friend userid


                                if (senderUidFound) {
                                    // Handle the case when senderUidToCheck exists for recipientUid
                                    // For example:
                                    // showToast("SenderUid exists for recipientUid")
                                    Toast.makeText(requireContext(), "Friend is already sent...", Toast.LENGTH_SHORT).show()
    //                                Log.d("TAG", "sender uid is present : $senderUidFound ")
                                } else {
                                    // logic from creating friend request.... method creating for this logic
                                    friendRequestSending(currentUserUid, recipientFriendRequestsRef, user)  // testing ..
    //                                Log.d("TAG", " Request sending.......... ")

                                }


                            } else {
                                // logic from creating friend request.... method creating for this logic
                                friendRequestSending(currentUserUid, recipientFriendRequestsRef, user)  // testing ..
    //                            Log.d("TAG", " Request sending.......... ")

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error if needed for recipientUid
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed for currentUserUid
            }
        })

    }

         private fun  friendRequestSending(currentUserUid: String?, recipientFriendRequestsRef: DatabaseReference, user: User) {
            // Retrieve name and image for the current user
            val currentUserRef = FirebaseDatabase.getInstance().getReference("user").child(currentUserUid!!)
            currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val senderName = snapshot.child("name").getValue(String::class.java)
                        val senderImage = snapshot.child("imgUri").getValue(String::class.java)
                        val status = "pending"

                        // Create a friend request object
                        val friendRequest = FriendRequest(null, user.uid, currentUserUid, senderName, senderImage, status)

                        // Reference the recipient user's friend_requests node directly using their UID
                        val requestId = recipientFriendRequestsRef.push().key // Generate a unique key for the friend request
                        friendRequest.requestId = requestId
                        recipientFriendRequestsRef.child(requestId!!).setValue(friendRequest)
                            .addOnSuccessListener {
                                // Friend request sent successfully
                                Toast.makeText(requireContext(), "Friend request sent!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                // Handle failure while sending friend request
                                Toast.makeText(requireContext(), "Failed to send friend request", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }


    }