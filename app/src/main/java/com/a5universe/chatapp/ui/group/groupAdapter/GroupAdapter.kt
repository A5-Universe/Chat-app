package com.a5universe.chatapp.ui.group.groupAdapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.group.GroupDetails
import com.a5universe.chatapp.ui.group.groupActivity.GroupChatActivity
import com.a5universe.chatapp.ui.group.groupFragment.GroupsFragment
import com.squareup.picasso.Picasso

class GroupAdapter(private val groupsFragment: GroupsFragment,
                   private val groupsArrayList: ArrayList<GroupDetails>) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(groupsFragment.context).inflate(R.layout.item_user_group, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groupsArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groupsArrayList[position]

        holder.groupName.text = group.groupName ?: "Empty Name"

        val imgUri = group.groupImage
        if (imgUri?.isNotEmpty() == true) {
            Picasso.get().load(imgUri).placeholder(R.drawable.user1).into(holder.groupImage)
        } else {
            // Handle empty or null image URL, load a placeholder or set default image
            val placeholderImageUrl =
                "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/group_placeholder.png?alt=media&token=your_token_here"
            Picasso.get().load(placeholderImageUrl).into(holder.groupImage)
        }

        // Handle group item click
        holder.itemView.setOnClickListener {
            val intent = Intent(groupsFragment.context, GroupChatActivity::class.java)
            intent.putExtra("groupName", group.groupName ?: "Empty Name")
            intent.putExtra("groupImage", group.groupImage ?: "")
            intent.putExtra("groupId", group.groupId ?: "")
            groupsFragment.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var groupImage: ImageView = itemView.findViewById(R.id.groupImage)
        var groupName: TextView = itemView.findViewById(R.id.groupName)
//        var lastMessage: TextView = itemView.findViewById(R.id.lastMsg) // last msg

    }
}