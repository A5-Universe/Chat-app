package com.a5universe.chatapp.ui.group.groupActivity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.a5universe.chatapp.databinding.ActivityGroupInfoBinding
import com.a5universe.chatapp.model.authentication.User
import com.a5universe.chatapp.model.connect.FriendRequest
import com.a5universe.chatapp.model.group.GroupChat
import com.a5universe.chatapp.model.group.GroupDetails
import com.a5universe.chatapp.ui.group.groupAdapter.UpdateAddMemberAdapter
import com.a5universe.chatapp.ui.group.groupAdapter.UpdateMemberAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class GroupInfoActivity : AppCompatActivity(), UpdateAddMemberAdapter.OnAddClickListener, UpdateMemberAdapter.OnRemoveClickListener  {
    lateinit var binding: ActivityGroupInfoBinding
    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var updateGroupAddMembersList: ArrayList<FriendRequest>
    private lateinit var reference: DatabaseReference
    private lateinit var updateGroupAddMemberAdapter: UpdateAddMemberAdapter
    private var currentUserUid: String? = null
    private lateinit var groupDetails: GroupDetails
    private lateinit var groupId: String
    private lateinit var updateMemberAdapter: UpdateMemberAdapter
    private var groupMembersList: MutableList<User> = mutableListOf()

    private val addedUserUids = HashSet<String>()  // Added parameter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //firebase initialization
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("friends")
        val usersRef = database.getReference("user")
        storage = FirebaseStorage.getInstance()
        currentUserUid = auth.currentUser?.uid

        // For Adding Group Member
        updateGroupAddMembersList = ArrayList()
        updateGroupAddMemberAdapter = UpdateAddMemberAdapter(this, updateGroupAddMembersList,this,addedUserUids)
        binding.rcvAddMember.layoutManager = LinearLayoutManager(this)
        binding.rcvAddMember.adapter = updateGroupAddMemberAdapter
        groupId = intent.getStringExtra("groupId") ?: ""

        //back button
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddMemberBar.setOnClickListener {
            if (binding.addMemberBar.visibility == View.GONE || binding.MemberBar.visibility == View.VISIBLE) {
                binding.addMemberBar.visibility = View.VISIBLE
                binding.MemberBar.visibility = View.GONE
            }
        }

        binding.btnMemberBar.setOnClickListener {
            if (binding.addMemberBar.visibility == View.VISIBLE || binding.MemberBar.visibility == View.GONE) {
                binding.addMemberBar.visibility = View.GONE
                binding.MemberBar.visibility = View.VISIBLE
            }
        }

        // profile pic set
        binding.changeImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10)
        }

        // save button
        binding.btnUpdate.setOnClickListener {
            //logic here
            updateGroup()
        }

        // fetch group details
        fetchGroupDetails(groupId)

        // Fetch friends to Adding the group
        fetchAddingGroupMembers(usersRef)


        // For Fetch Group Member
        // Initialize the UpdateMemberAdapter
        updateMemberAdapter = UpdateMemberAdapter(groupMembersList,this)
        binding.rcvMember.layoutManager = LinearLayoutManager(this)
        binding.rcvMember.adapter = updateMemberAdapter

        // Fetch group member list
        fetchGroupMembers(groupId)



    }

// Function to fetch group members using the groupId
    private fun fetchGroupMembers(groupId: String) {
    val groupsRef = database.getReference("groups").child(groupId)

    groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val groupDetails = snapshot.getValue(GroupDetails::class.java)

            groupDetails?.let {
                val membersMap = it.members

                if (membersMap != null) {
                    val memberUids = membersMap.keys.toList()

                    // Fetch member details using UIDs
                    fetchMembersDetails(memberUids)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error if needed
        }
    })
}


    private fun fetchMembersDetails(memberUids: List<String?>) {
        val usersRef = database.getReference("user")

        for (uid in memberUids) {
            usersRef.child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    val user = userSnapshot.getValue(User::class.java)

                    user?.let {
                        groupMembersList.add(it)

                        // Notify the adapter when all members are fetched
                        if (groupMembersList.size == memberUids.size) {
                            updateMemberAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }
    }

    // Implement the interface method for removing a member
    override fun onRemoveClick(user: User) {
        // Handle the "Remove" button click for removing a member from the group
        // You can remove the user from the group or perform any other necessary actions
        removeMemberFromGroup(user)
    }

    // Function to remove a member from the group in the database
    private fun removeMemberFromGroup(user: User) {
        val memberUid = user.uid

        // Remove the member from the group in the database
        val groupMembersRef = database.getReference("groups").child(groupId).child("members")
        groupMembersRef.child(memberUid!!).removeValue()
            .addOnSuccessListener {
                // Member successfully removed from the group in the database

                // Update the UI if needed
                groupMembersList.remove(user)
                updateMemberAdapter.notifyDataSetChanged()

                Toast.makeText(this, "Removed member: ${user.name} from the group", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle the failure to remove the member
                Toast.makeText(this, "Failed to remove member from the group", Toast.LENGTH_SHORT).show()
            }
    }

//    test

    // Function to fetch group details using the groupId
    private fun fetchGroupDetails(groupId: String) {
        val groupsRef = database.getReference("groups").child(groupId)
        groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupDetails = snapshot.getValue(GroupDetails::class.java) ?: GroupDetails()

                // Set the fetched group name and image in the UI
                binding.groupName.setText(groupDetails.groupName)
                if (groupDetails.groupImage?.isNotEmpty() == true) {
                    Picasso.get().load(groupDetails.groupImage).into(binding.groupImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    // Function to initiate the group updating process
    private fun updateGroup() {
        // Get other group details (e.g., group name, admin name, admin UID)
        val groupName = binding.groupName.text.toString().trim()
        val adminUid = currentUserUid

        fetchAdminNameAndUpdateGroup(groupName, adminUid!!)
    }

    // Function to fetch admin name and update the group
    private fun fetchAdminNameAndUpdateGroup(groupName: String, adminUid: String) {
        val userRef = database.getReference("user").child(adminUid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adminName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                updateGroupMain(groupName, adminName,adminUid)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    // Function to update the group details in the database
    private fun updateGroupMain(groupName: String, adminName: String, adminUid: String) {
        // Validate group name
        if (groupName.isEmpty()) {
            Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show()
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


        private fun saveGroupToDatabase(groupName: String, adminName: String, adminUid: String, imageUrl: String?){
            val membersMap = mutableMapOf<String?, Boolean>()


            // Add members from groupMembersList to the membersMap
            for (member in updateGroupAddMembersList) {
                if (addedUserUids.contains(member.senderUid)) {
                    membersMap[member.senderUid] = true
                }
            }
            // Add the admin user to the membersMap
            membersMap[adminUid] = true
            Log.d("TAG", "saveGroupToDatabase: $membersMap")

            // Create a map for group chat (initially empty)
            val groupChatMap = emptyMap<String, GroupChat>()
            val noImageUrl =
                "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"


        // Update groupDetails with new values
        groupDetails.groupName = groupName
        groupDetails.groupImage = imageUrl ?: groupDetails.groupImage ?: noImageUrl
        groupDetails.adminName = adminName
        groupDetails.adminUid = adminUid
        groupDetails.members = membersMap
        groupDetails.groupChat = groupChatMap

        // Show ProgressBar while updating
        binding.idProgress.visibility = View.VISIBLE

        // Update the group details in the database
        val groupsRef = database.getReference("groups").child(groupId)
        groupsRef.setValue(groupDetails)
            .addOnCompleteListener { task ->
                binding.idProgress.visibility = View.GONE

                if (task.isSuccessful) {
                    Toast.makeText(this, "Group updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after updating the group
                } else {
                    Toast.makeText(this, "Failed to update group", Toast.LENGTH_SHORT).show()
                }
            }

    }




    // Function to fetch friends for the group members list
    private fun fetchAddingGroupMembers(usersRef: DatabaseReference) {
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
                                updateGroupAddMembersList.clear()
                                updateGroupAddMembersList.addAll(friendsList)
                                updateGroupAddMemberAdapter.notifyDataSetChanged()
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

    // For select Image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                selectedImageUri = data.data
                binding.groupImage.setImageURI(selectedImageUri)
            }
        }
    }


    // Function to add a member to the group
    private fun addMemberToGroup(memberUid: String) {
        // Implement the logic to add the member to the group
        // You can update the UI, update the database, or perform any other necessary actions
        Log.d("TAG", "onAddClick = Friend UID to add: $memberUid")
        Toast.makeText(this, "Added member with UID: $memberUid to the group", Toast.LENGTH_SHORT).show()
        addedUserUids.add(memberUid)

    }

    //     Implement the interface method
    override fun onAddClick(user: FriendRequest, isSelected: Boolean) {
        // Handle the "Add" button click for creating a group
        // You can add the user to the group or perform any other necessary actions
        if (isSelected) {
            addMemberToGroup(user.senderUid!!)
        }
    }



}