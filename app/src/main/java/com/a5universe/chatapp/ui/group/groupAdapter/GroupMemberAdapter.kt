package com.a5universe.chatapp.ui.group.groupAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.connect.FriendRequest
import com.a5universe.chatapp.ui.group.groupActivity.GroupCreateActivity
import com.squareup.picasso.Picasso

class GroupMemberAdapter(
    private val groupInfoActivity: GroupCreateActivity,
    private val usersArrayList: ArrayList<FriendRequest>,
    private val addClickListener: OnAddClickListener,
    private val addedUserUids: HashSet<String>  // Added parameter
):RecyclerView.Adapter<GroupMemberAdapter.ViewHolder>() {


    interface OnAddClickListener {
        fun onAddClick(user: FriendRequest, isSelected: Boolean)
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupMemberAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_find_friends, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupMemberAdapter.ViewHolder, position: Int) {
        val users = usersArrayList[position]

        holder.userName.text = users.senderName ?: "Empty Name"

        // Load user image using Picasso or any other image loading library
        val imgUri = users.senderImage
        if (imgUri?.isNotEmpty() == true) {
            Picasso.get().load(imgUri).placeholder(R.drawable.user1).into(holder.userImage)
        } else {
            val placeholderImageUrl =
                "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
            Picasso.get().load(placeholderImageUrl).into(holder.userImage)
        }

        // Check whether the user has already been added
        if (addedUserUids.contains(users.senderUid)) {
            // User already added, disable the "Add" button
            holder.btnAdd.isEnabled = false
        } else {
            // User not added, enable the "Add" button
            holder.btnAdd.isEnabled = true

            // Set click listener to add the user to the group
            holder.btnAdd.setOnClickListener {
                // Notify the activity through the callback interface
                addClickListener.onAddClick(users,holder.btnAdd.isEnabled)

                // Add the user UID to the set of added users
//                addedUserUids.add(users.senderUid!!)

                // Disable the button after clicking
                holder.btnAdd.isEnabled = false
            }
        }

    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImage: ImageView = itemView.findViewById(R.id.userImage)
        var userName: TextView = itemView.findViewById(R.id.userName)
        var btnAdd: Button = itemView.findViewById(R.id.btnAdd)
    }

}