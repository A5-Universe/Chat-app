    package com.a5universe.chatapp.ui.group.groupActivity

    import android.content.Intent
    import android.net.Uri
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.view.View
    import android.widget.Toast
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.a5universe.chatapp.databinding.ActivityGroupCreateBinding
    import com.a5universe.chatapp.model.connect.FriendRequest
    import com.a5universe.chatapp.model.group.GroupChat
    import com.a5universe.chatapp.model.group.GroupDetails
    import com.a5universe.chatapp.ui.group.groupAdapter.GroupMemberAdapter
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import com.google.firebase.storage.FirebaseStorage
    import com.google.firebase.storage.StorageReference
    import com.google.firebase.storage.UploadTask

    class GroupCreateActivity : AppCompatActivity(), GroupMemberAdapter.OnAddClickListener{
        lateinit var binding: ActivityGroupCreateBinding
        private var selectedImageUri: Uri? = null
        private lateinit var auth: FirebaseAuth
        private lateinit var database: FirebaseDatabase
        private lateinit var storage: FirebaseStorage
        private lateinit var groupMembersList: ArrayList<FriendRequest>
        private lateinit var reference: DatabaseReference
        private lateinit var groupMemberAdapter: GroupMemberAdapter
        private var currentUserUid: String? = null
        // Keep track of added user UIDs at the activity level
        private val addedUserUids = HashSet<String>()


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityGroupCreateBinding.inflate(layoutInflater)
            setContentView(binding.root)

            //firebase initialization
            auth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("friends")
            val usersRef = database.getReference("user")
            currentUserUid = auth.currentUser?.uid
            storage = FirebaseStorage.getInstance()
            groupMembersList = ArrayList()
            groupMemberAdapter = GroupMemberAdapter(this, groupMembersList,this,addedUserUids)
            binding.rcvAddMember.layoutManager = LinearLayoutManager(this)
            binding.rcvAddMember.adapter = groupMemberAdapter

            // Fetch friends to populate the group members list
            fetchFriendsForGroupMembers(usersRef)

            //back button
            binding.btnBack.setOnClickListener {
                onBackPressed()
            }

            // profile pic set
            binding.changeImage.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
            }

            // save button
            binding.btnSave.setOnClickListener {
                //logic here
                saveGroup()
            }
        }

        override fun onAddClick(user: FriendRequest, isSelected: Boolean) {
            if (isSelected) {
                addMemberToGroup(user.senderUid!!)
            } else {
                removeMemberFromGroup(user.senderUid!!)
            }

        }

        private fun fetchFriendsForGroupMembers(usersRef: DatabaseReference) {
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

                            // Fetch friend's name and image from the users node
                            usersRef.child(friendUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val friendName = userSnapshot.child("name").getValue(String::class.java)
                                    val friendImage = userSnapshot.child("imgUri").getValue(String::class.java)

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
                                    groupMembersList.clear()
                                    groupMembersList.addAll(friendsList)
                                    groupMemberAdapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error if needed
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == 10) {
                if (resultCode == RESULT_OK && data != null) {
                    selectedImageUri = data.data
                    binding.groupImg.setImageURI(selectedImageUri)
                }
            }
        }


        private fun addMemberToGroup(memberUid: String) {
            // Implement the logic to add the member to the group
            // You can update the UI, update the database, or perform any other necessary actions
//            Log.d("TAG", "onAddClick = Friend UID to add: $memberUid")

            // Add the user UID to the set of added users
            addedUserUids.add(memberUid)
        }
        // Function to remove a member from the group
        private fun removeMemberFromGroup(memberUid: String) {
            // Implement the logic to remove the member from the group
            // You can update the UI, update the database, or perform any other necessary actions
//            Toast.makeText(this, "Removed member with UID: $memberUid from the group", Toast.LENGTH_SHORT).show()

            // Remove the user UID from the set of added users
            addedUserUids.remove(memberUid)
        }


        private fun saveGroup() {
            // Get other group details (e.g., group name, admin name, admin UID)
            val groupName = binding.groupName.text.toString().trim()
            val adminUid = currentUserUid
            fetchAdminNameAndSaveGroup(groupName, adminUid!!)
        }

        private fun fetchAdminNameAndSaveGroup(groupName: String, adminUid: String) {
            val userRef = database.getReference("user").child(adminUid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val adminName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    saveGroupMain(groupName, adminName, adminUid)  // call main logic function
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }

        private fun saveGroupMain(groupName: String, adminName: String, adminUid: String) {
            // Validate group name
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show()
                return
            }

            // Validate group members
            if (groupMembersList.isEmpty()) {
                Toast.makeText(this, "Please add at least one group member", Toast.LENGTH_SHORT).show()
                return
            }

            // Create a unique filename for the image using a timestamp
            val imageFileName = "group_image_${System.currentTimeMillis()}.jpg"
            val storageRef: StorageReference = storage.reference.child("group_images/$imageFileName")
            var uploadedImageUrl: String? = null

            if (selectedImageUri != null) {
                // Upload the image to Firebase Storage

                val uploadTask: UploadTask = storageRef.putFile(selectedImageUri!!)

                // Continue with the upload task
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        uploadedImageUrl = downloadUri.toString()
                        saveGroupToDatabase(groupName, adminName, adminUid, uploadedImageUrl)
                    } else {
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // No image selected, save group to database with no image URL
                saveGroupToDatabase(groupName, adminName, adminUid, null)
            }
        }

        private fun saveGroupToDatabase(groupName: String, adminName: String, adminUid: String, imageUrl: String?) {

                val membersMap = mutableMapOf<String?, Boolean>()

                // Add selected members from groupMembersList to the membersMap
                for (member in groupMembersList) {
                    if (addedUserUids.contains(member.senderUid)) {
                        membersMap[member.senderUid] = true
                    }
                }
                // Add the admin user to the membersMap
                membersMap[adminUid] = true
//                Log.d("TAG", "saveGroupToDatabase: $membersMap")

                // Create a map for group chat (initially empty)
                val groupChatMap = emptyMap<String, GroupChat>()
                val noImageUrl =
                    "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"

                // Create a GroupDetails object
                val groupDetails = GroupDetails(
                    groupId = "", // You may generate a unique ID or use Firebase push ID
                    groupName = groupName,
                    groupImage = imageUrl ?: noImageUrl, // Use the download URL of the uploaded image or no image URL
                    adminName = adminName,
                    adminUid = adminUid,
                    members = membersMap,
                    groupChat = groupChatMap
                )

                // Save the group details to the database
                val groupsRef = database.getReference("groups")
                val newGroupRef = groupsRef.push()

                // Get the generated group ID
                val groupId = newGroupRef.key

                // Update the groupDetails with the generated group ID
                groupDetails.groupId = groupId

                newGroupRef.setValue(groupDetails)
                    .addOnCompleteListener { task ->
                        binding.idProgress.visibility = View.GONE

                        if (task.isSuccessful) {
                            Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity after creating the group
                        } else {
                            Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show()
                        }
                    }
            }



    }

















