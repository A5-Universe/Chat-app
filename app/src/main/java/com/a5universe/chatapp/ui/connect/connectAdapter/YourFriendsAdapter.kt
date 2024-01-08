package com.a5universe.chatapp.ui.connect.connectAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.connect.FriendRequest
import com.a5universe.chatapp.ui.connect.connectFragment.YourFriendsFragment
import com.squareup.picasso.Picasso

class YourFriendsAdapter(
    private val yourFriendsFragment: YourFriendsFragment,
    private val usersArrayList: ArrayList<FriendRequest>,
    private val friendsActionListener: OnFriendsActionListener
) :
        RecyclerView.Adapter<YourFriendsAdapter.ViewHolder>() {

    interface OnFriendsActionListener {
        fun onChatFriend(user: FriendRequest)
        fun onDeleteFriend(user: FriendRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_your_friends, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users = usersArrayList[position]

        holder.senderName.text = users.senderName ?: "Empty Name"

        // Load user image using Picasso or any other image loading library
        val imgUri = users.senderImage
        if (imgUri?.isNotEmpty() == true) {
            Picasso.get().load(imgUri).placeholder(R.drawable.user1).into(holder.senderImage)
        } else {
            val placeholderImageUrl =
                    "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
            Picasso.get().load(placeholderImageUrl).into(holder.senderImage)
        }

        holder.chatButton.setOnClickListener {
            friendsActionListener.onChatFriend(users)
        }
        holder.deleteButton.setOnClickListener {
            friendsActionListener.onDeleteFriend(users)
        }
    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderImage: ImageView = itemView.findViewById(R.id.senderImage)
        var senderName: TextView = itemView.findViewById(R.id.senderName)
        var chatButton: Button = itemView.findViewById(R.id.btnChat)
        var deleteButton: Button = itemView.findViewById(R.id.btnDelete)

    }

}