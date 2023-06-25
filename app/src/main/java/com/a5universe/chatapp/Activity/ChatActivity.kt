package com.a5universe.chatapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.Adapter.MessagesAdapter
import com.a5universe.chatapp.Model.Messages
import com.a5universe.chatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.Date

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding

    private var receiverImage: String? = null
    private var receiverUID: String? = null
    private var receiverName: String? = null
    private var senderUID: String? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var msgRecyclerView: RecyclerView
    var messagesArrayList = ArrayList<Messages>()
    lateinit var adapter: MessagesAdapter
    companion object {
        var sImage: String? = null
        var rImage: String? = null
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        receiverName = intent.getStringExtra("name")
        receiverImage = intent.getStringExtra("ReceiverImage")
        receiverUID = intent.getStringExtra("uid")

        Picasso.get().load(receiverImage).into(binding.friendImg)
        binding.friendName.text = receiverName


        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        // Initialize the RecyclerView from the layout
        msgRecyclerView = binding.msgRecyclerView // Updated variable assignment

        msgRecyclerView.layoutManager = linearLayoutManager
        //adapter = MessagesAdapter(this, messagesArrayList,sImage)
        adapter = MessagesAdapter(this, messagesArrayList)
        msgRecyclerView.adapter = adapter


        senderUID = firebaseAuth.uid
        senderRoom = senderUID + receiverUID
        receiverRoom = receiverUID + senderUID

        val reference: DatabaseReference = database.reference.child("user").child(firebaseAuth.uid!!)
        val chatReference: DatabaseReference = database.reference.child("chats").child(senderRoom).child("messages")

        // image set on chat function..
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sImage = snapshot.child("imgUri").value.toString()
                rImage = receiverImage
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // for chat
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


    binding.btnSend.setOnClickListener {
        val message = binding.edtTextMsg.text.toString()

        if (message.isEmpty()) {
        Toast.makeText(this, "Write Message", Toast.LENGTH_SHORT).show()
        }
        else {
            binding.edtTextMsg.setText("")
            val date = Date()
            val messages = Messages(message, senderUID!!, date.time)

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
                        .addOnCompleteListener { }
                }

            }
}

    binding.btnBack.setOnClickListener {
        onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}