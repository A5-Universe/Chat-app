package com.a5universe.chatapp.ui.group.groupAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.authentication.User
import com.squareup.picasso.Picasso


class UpdateMemberAdapter(
    private val membersList: List<User>,
    private val onRemoveClickListener: OnRemoveClickListener
) : RecyclerView.Adapter<UpdateMemberAdapter.ViewHolder>() {

    interface OnRemoveClickListener {
        fun onRemoveClick(user: User)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_members, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = membersList[position]
        holder.userName.text = member.name

        // Load member image using Picasso or Glide
        if (member.imgUri != null && member.imgUri!!.isNotEmpty()) {
            Picasso.get().load(member.imgUri).into(holder.userImage)
        } else {
            // You can set a default image or handle it based on your requirements
            holder.userImage.setImageResource(R.drawable.user1)
        }

        // Set the click listener for the remove button
        holder.btnRemove.setOnClickListener {
            onRemoveClickListener.onRemoveClick(member)
        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemove)
    }
}