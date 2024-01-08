package com.a5universe.chatapp.ui.chat.chatActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.ui.chat.chatAdapter.MessagesAdapter
import com.a5universe.chatapp.databinding.ActivityChatBinding
import com.a5universe.chatapp.model.chat.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var receiverImage: String? = null
    private var receiverUID: String? = null
    private var receiverName: String? = null
    private var senderUID: String? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var msgRecyclerView: RecyclerView
    var messagesArrayList = ArrayList<Messages>()
    lateinit var adapter: MessagesAdapter
    private val handler = Handler()

    companion object {
        var sImage: String? = null
        var rImage: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Extract data from intent
        receiverName = intent.getStringExtra("name")
        receiverImage = intent.getStringExtra("ReceiverImage")
        receiverUID = intent.getStringExtra("uid")

        //Set user data
        if (receiverImage?.isEmpty() == true) {
            // Handle empty or null image URL, load a placeholder or set default image
            val placeholderImageUrl = "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
            Picasso.get().load(placeholderImageUrl).into(binding.friendImg)
        } else {
            Picasso.get().load(receiverImage).into(binding.friendImg)
        }
        binding.friendName.text = receiverName

        // Initialize RecyclerView and adapter
        msgRecyclerView = binding.msgRecyclerView // Updated variable assignment
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true

        // Initialize the RecyclerView from the layout
        msgRecyclerView.layoutManager = linearLayoutManager
        adapter = MessagesAdapter(this, messagesArrayList)
        msgRecyclerView.adapter = adapter

        // Get sender and receiver UID
        senderUID = auth.uid
        senderRoom = "$senderUID$receiverUID"
        receiverRoom = "$receiverUID$senderUID"

        // image set on chat function.
        val reference: DatabaseReference = database.reference.child("user").child(auth.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sImage = snapshot.child("imgUri").value.toString()
                rImage = receiverImage
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Set click listeners for send button and back button
        binding.btnSend.setOnClickListener {
            sendButtonWork()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // Set up listener for chat messages
        setupChatMessageListener()

        // Set up Friend status online/offline or Typing
        setupFriendStatusListener()

        // Set up User Typing or Not
        setupTypingListener()

        binding.btnCall.setOnClickListener {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_LONG).show()
        }
        binding.btnVideo.setOnClickListener {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_LONG).show()
        }
        binding.btnMenu.setOnClickListener {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_LONG).show()
        }
        binding.btnMore.setOnClickListener {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_LONG).show()
        }


    }

    private fun setupTypingListener() {
        binding.edtTextMsg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used in this example
            }

            override fun afterTextChanged(s: Editable?) {

                // Reference to the typing status in the sender's node
                val typingStatusReference: DatabaseReference =
                    database.reference.child("user").child(senderUID!!).child("typing")

                // Set typing status to "typing"
                typingStatusReference.setValue("typing")

                // Remove existing callbacks (cancel any pending delay)
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)

            }
            var userStoppedTyping = Runnable {
                database.reference.child("user")
                    .child(senderUID!!).child("typing")
                    .setValue("non_typing")
            }
        })
    }

    private fun setupFriendStatusListener() {
        // Reference to the presence status in the receiver's node
        val presenceStatusReference: DatabaseReference =
            database.reference.child("user").child(receiverUID!!).child("status")

        // Reference to the typing status in the receiver's node
        val typingStatusReference: DatabaseReference =
            database.reference.child("user").child(receiverUID!!).child("typing")

        // Set up presence status listener
        presenceStatusReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val status = snapshot.getValue(String::class.java)
                    if (status == null || status == "online") {
                        // Handle the case where the "status" parameter is not present or the user is online
                        binding.userStatus.text = "Online"
                        binding.userStatus.setTextColor(
                            ContextCompat.getColor(
                                this@ChatActivity,
                                R.color.green
                            )
                        )
                    } else {
                        // User is offline
                        binding.userStatus.text = "Offline"
                        binding.userStatus.setTextColor(
                            ContextCompat.getColor(
                                this@ChatActivity,
                                R.color.red
                            )
                        )
                    }
                } else {
                    // User status node not present, assume offline
                    binding.userStatus.text = "Offline"
                    binding.userStatus.setTextColor(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.red
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Set up typing status listener
        // Set up typing status listener
        typingStatusReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val typingStatus = snapshot.getValue(String::class.java)
                if (typingStatus == "typing") {
                    // Handle friend typing
                    // For example, you can show a typing indicator
                    binding.userStatus.text = "Typing..."
                    // Set text color to green when online (typing)
                    binding.userStatus.setTextColor(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.green
                        )
                    )
                } else if (typingStatus == "non_typing") {
                    // Handle friend not typing

                    // Set text color to the appropriate color when online (not typing)
                    checkFriendOnlineStatus()

                } else {
                    // Handle friend not typing
                    // Set text color to the appropriate color when online (not typing)
                    checkFriendOnlineStatus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

    }

    private fun checkFriendOnlineStatus() {
        val presenceStatusReference: DatabaseReference =
            database.reference.child("user").child(receiverUID!!).child("status")

        presenceStatusReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val status = snapshot.getValue(String::class.java)
                    if (status == null || status == "online") {
                        // Friend is online
                        binding.userStatus.text = "Online"
                        binding.userStatus.setTextColor(
                            ContextCompat.getColor(
                                this@ChatActivity,
                                R.color.green
                            )
                        )
                    } else {
                        // Friend is offline
                        binding.userStatus.text = "Offline"
                        binding.userStatus.setTextColor(
                            ContextCompat.getColor(
                                this@ChatActivity,
                                R.color.red
                            )
                        )
                    }
                } else {
                    // Friend status node not present, assume offline
                    binding.userStatus.text = "Offline"
                    binding.userStatus.setTextColor(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.red
                        )
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun setupChatMessageListener() {
            // Listen for changes in chat messages
            val chatReference: DatabaseReference = database.reference.child("chats").child(senderRoom).child("messages")
            // For chat
        chatReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesArrayList.clear() // for clear message
                for (dataSnapshot in snapshot.children) {
                    val messages = dataSnapshot.getValue(Messages::class.java)
                    messagesArrayList.add(messages!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendButtonWork() {
        val message = binding.edtTextMsg.text.toString()
        if (message.isEmpty()) {
            Toast.makeText(this, "Write Message", Toast.LENGTH_SHORT).show()
        }
        else {
            // Clear the EditText after sending the message
            binding.edtTextMsg.text.clear()

            // Get current timestamp
            val timestamp = System.currentTimeMillis()
            val messages = Messages(
                message=message,
                senderId= senderUID!!,
                timeStamp= timestamp)

            database.reference.child("chats")
                .child(senderRoom)
                .child("messages")
                .push()
                .setValue(messages)
                .addOnCompleteListener {
                    database.reference.child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .push()
                        .setValue(messages)
                        .addOnCompleteListener {  task ->
                            if (task.isSuccessful) {
                                // Message sent to receiver's chat room, you can handle success here

                                // Scroll to the last item in the RecyclerView
                                msgRecyclerView.scrollToPosition(adapter.itemCount - 1)
                            } else {
                                // Handle failure to send message to receiver's chat room
                            }
                        }
                    }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        // Set user status to online when activity is resumed
        updateUserStatus("online")
    }
    override fun onPause() {
        super.onPause()
        // Set user status to offline when activity is paused
        updateUserStatus("offline")
    }

    private fun updateUserStatus(status: String) {
        database.reference.child("user").child(auth.uid!!).child("status").setValue(status)
    }

}