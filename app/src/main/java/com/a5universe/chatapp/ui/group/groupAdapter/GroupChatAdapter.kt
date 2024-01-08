package com.a5universe.chatapp.ui.group.groupAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.group.GroupChat
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class GroupChatAdapter(
    private val context: Context,
    private val groupChatList: ArrayList<GroupChat>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId: String? = auth.currentUser?.uid

    private val ITEM_SEND = 1
    private val ITEM_RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_SEND -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.item_sender_group_message, parent, false)
                SenderViewHolder(view)
            }
            else -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.item_receiver_group_message, parent, false)
                ReceiverViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val groupChat = groupChatList[position]

        when (holder) {
            is SenderViewHolder -> {
                holder.bindSender(groupChat)
            }
            is ReceiverViewHolder -> {
                holder.bindReceiver(groupChat)
            }
        }
    }

    override fun getItemCount(): Int {
        return groupChatList.size
    }

    override fun getItemViewType(position: Int): Int {
        val groupChat = groupChatList[position]
        return if (currentUserId == groupChat.senderId) {
            ITEM_SEND
        } else {
            ITEM_RECEIVE
        }
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindSender(groupChat: GroupChat) {
            itemView.findViewById<TextView>(R.id.CurrentUserName).text = groupChat.senderName
            Picasso.get().load(groupChat.senderImage).into(itemView.findViewById<ImageView>(R.id.CurrentUserImage))
            itemView.findViewById<TextView>(R.id.CurrentUserMessage).text = groupChat.message
        }
    }

    inner class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindReceiver(groupChat: GroupChat) {
            itemView.findViewById<TextView>(R.id.OtherUserName).text = groupChat.senderName
            Picasso.get().load(groupChat.senderImage).into(itemView.findViewById<ImageView>(R.id.OtherUserImage))
            itemView.findViewById<TextView>(R.id.OtherUserMessage).text = groupChat.message
        }
    }
}