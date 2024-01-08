package com.a5universe.chatapp.ui.chat.chatAdapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.ui.chat.chatActivity.ChatActivity
import com.a5universe.chatapp.ui.chat.chatFragment.ChatFragment
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.connect.FriendRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import com.google.firebase.database.*


class UserChatAdapter (private val chatFragment: ChatFragment,
                       private val friendRequestsList: ArrayList<FriendRequest>) :
    RecyclerView.Adapter<UserChatAdapter.ViewHolder>()
{

//        test
//fun updateFriendRequestsList(newList: List<FriendRequest>) {
//    friendRequestsList.clear()
//    friendRequestsList.addAll(newList)
//    notifyDataSetChanged()
//}
//        test

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(chatFragment.requireContext())
            .inflate(R.layout.item_user_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friendRequestsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

                val friendRequest  = friendRequestsList[position]
                holder.userName.text = friendRequest.senderName?: "Empty Name"

                val imgUri = friendRequest.senderImage
                if (imgUri?.isNotEmpty() == true) {
                Picasso.get().load(imgUri).placeholder(R.drawable.user1).into(holder.userImage)
                } else {
                    // Handle empty or null image URL, load a placeholder or set default image
                    val placeholderImageUrl = "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
                    Picasso.get().load(placeholderImageUrl).into(holder.userImage)
                }

                // Display & Fetch the last message
                fetchLastMessage(friendRequest.senderUid, holder.lastMessage) // test


                //intent passing for chat activity
                holder.itemView.setOnClickListener {
                val intent = Intent(chatFragment.requireContext(), ChatActivity::class.java)
                intent.putExtra("name", friendRequest.senderName ?: "Empty Name")
                intent.putExtra("ReceiverImage", friendRequest.senderImage ?: "")
                intent.putExtra("uid", friendRequest.senderUid ?: "")
                chatFragment.startActivity(intent)
            }
        }

    //    test..
    private fun fetchLastMessage(friendUid: String?, lastMessageTextView: TextView) {
        val currentUserUid = auth.currentUser?.uid

        if (friendUid != null && currentUserUid != null) {

            // Create two possible chat IDs by sorting concatenated UIDs
            val chatId1 = currentUserUid + friendUid
            val chatId2 = friendUid + currentUserUid

            Log.d("TAG", "chatId1: $chatId1, chatId2: $chatId2")

            val messagesReference1: DatabaseReference =
                database.getReference("chats")
                    .child(chatId1)
                    .child("messages")

            val messagesReference2: DatabaseReference =
                database.getReference("chats")
                    .child(chatId2)
                    .child("messages")

            // Check if both chat IDs exist
            messagesReference1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chat1Exists = snapshot.exists()

                    messagesReference2.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chat2Exists = snapshot.exists()

                            // Use the valid chat ID
                            val messagesReference = if (chat1Exists) {
                                messagesReference1
                            } else if (chat2Exists) {
                                messagesReference2
                            } else {
                                // Handle the case where neither chat ID exists
                                return
                            }

                            // Fetch messages using the valid chat ID
                            messagesReference.orderByChild("timeStamp").limitToLast(1)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (childSnapshot in snapshot.children) {
                                            val message =
                                                childSnapshot.child("message").getValue(String::class.java)
                                            lastMessageTextView.text = message
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle error if needed
                                    }
                                })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error if needed
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImage: ImageView = itemView.findViewById(R.id.userImage)
        var userName: TextView = itemView.findViewById(R.id.userName)
        var lastMessage: TextView = itemView.findViewById(R.id.lastMsg) // last msg
    }

}




