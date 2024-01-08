    package com.a5universe.chatapp.ui.group.groupActivity

    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.widget.Toast
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.a5universe.chatapp.databinding.ActivityGroupChatBinding
    import com.a5universe.chatapp.model.group.GroupChat
    import com.a5universe.chatapp.ui.group.groupAdapter.GroupChatAdapter
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.DatabaseReference
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import com.google.firebase.storage.FirebaseStorage
    import com.google.firebase.storage.StorageReference
    import com.squareup.picasso.Picasso

    class GroupChatActivity : AppCompatActivity() {
        private lateinit var binding: ActivityGroupChatBinding
        private lateinit var auth: FirebaseAuth
        private lateinit var database: FirebaseDatabase
        private lateinit var storage: FirebaseStorage
        private lateinit var groupChatList: ArrayList<GroupChat>
        private lateinit var adapter: GroupChatAdapter
        private lateinit var reference: DatabaseReference
    //    private lateinit var storageReference: StorageReference
        private var currentUserId: String? = null
        private var groupId: String? = null
        private var groupName: String? = null
        private var groupImage: String? = null
        private lateinit var rcvGroupMessage: RecyclerView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityGroupChatBinding.inflate(layoutInflater)
            setContentView(binding.root)

            auth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()
            storage = FirebaseStorage.getInstance()
            currentUserId = auth.currentUser?.uid
            groupId = intent.getStringExtra("groupId")
            groupName = intent.getStringExtra("groupName")
            groupImage = intent.getStringExtra("groupImage")

            //Set user data
            binding.groupName.text = groupName

            if (groupImage?.isEmpty() == true) {
                // Handle empty or null image URL, load a placeholder or set default image
                val placeholderImageUrl = "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
                Picasso.get().load(placeholderImageUrl).into(binding.groupImage)
            } else {
                Picasso.get().load(groupImage).into(binding.groupImage)
            }

            reference = database.getReference("groups").child(groupId!!).child("groupChat")
    //        storageReference = storage.reference.child("group_images")

            groupChatList = ArrayList()

            rcvGroupMessage = binding.rcvGroupMessage
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.stackFromEnd = true

            rcvGroupMessage.layoutManager = linearLayoutManager
            adapter = GroupChatAdapter(this, groupChatList)
            rcvGroupMessage.adapter = adapter



            // Set click listeners for send button and back button
            binding.btnSend.setOnClickListener {
                sendMessage()
            }

            binding.btnBack.setOnClickListener {
                onBackPressed()
            }
            binding.groupInfo.setOnClickListener{
                val groupInfoIntent = Intent(this, GroupInfoActivity::class.java)

                // Pass group image and group name as extras in the intent
                groupInfoIntent.putExtra("groupId", groupId)
                startActivity(groupInfoIntent)

            }

            fetchGroupChat()

            binding.btnAttach.setOnClickListener {
                //            openImageChooser()
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
            }

            binding.btnMenu.setOnClickListener {
                //            openImageChooser()
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
            }




        }


    private fun sendMessage() {
        val message = binding.edtTextMsg.text.toString().trim()

        if (message.isNotEmpty()) {
            val timestamp = System.currentTimeMillis().toString()
            val messageId = reference.push().key // Generate a randomly generated ID

            // Get the sender's name and image URL from the User node
            val senderRef = database.getReference("user").child(currentUserId!!)
            senderRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val senderName = snapshot.child("name").value.toString()
                    val senderImage = snapshot.child("imgUri").value.toString()

                    // Create GroupChat object with sender's name and image
                    val groupChat = GroupChat(
                        message = message,
                        senderId = currentUserId!!,
                        senderName = senderName,
                        senderImage = senderImage,
                        messageId = messageId,
                        timestamp = timestamp
                    )

                    // Update GroupChat node
                    reference.child(messageId!!).setValue(groupChat)
                    binding.edtTextMsg.text.clear()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        } else {
            Toast.makeText(this, "Write Message", Toast.LENGTH_SHORT).show()
        }
    }



    //    private fun openImageChooser() {
    //        val intent = Intent()
    //        intent.type = "image/*"
    //        intent.action = Intent.ACTION_GET_CONTENT
    //        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1)
    //    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //        super.onActivityResult(requestCode, resultCode, data)
    //
    //        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
    //            val imageUri = data.data
    //            uploadImage(imageUri!!)
    //        }
    //    }

    //    private fun uploadImage(imageUri: Uri) {
    //        val timestamp = System.currentTimeMillis().toString()
    //        val storageRef = storageReference.child("$timestamp.jpg")
    //
    //        storageRef.putFile(imageUri)
    //            .addOnSuccessListener {
    //                storageRef.downloadUrl.addOnSuccessListener { uri ->
    //                    val imageUrl = uri.toString()
    //
    //                    val groupChat = GroupChat(currentUserId, null, timestamp, imageUrl)
    //                    reference.child(timestamp).setValue(groupChat)
    //                }
    //            }
    //            .addOnFailureListener {
    //                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
    //            }
    //    }

        private fun fetchGroupChat() {
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupChatList.clear()

                    for (dataSnapshot in snapshot.children) {
                        val groupChat = dataSnapshot.getValue(GroupChat::class.java)
                        if (groupChat != null) {
                            groupChatList.add(groupChat)
                        }
                    }

                    groupChatList.sortBy { it.messageId  } // Ensure messages are sorted by timestamp

                    adapter.notifyDataSetChanged()
                    binding.rcvGroupMessage.scrollToPosition(groupChatList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }


    }