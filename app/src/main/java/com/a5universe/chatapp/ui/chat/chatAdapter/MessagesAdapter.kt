package com.a5universe.chatapp.ui.chat.chatAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.ui.chat.chatActivity.ChatActivity
import com.a5universe.chatapp.ui.chat.chatActivity.ChatActivity.Companion.rImage
import com.a5universe.chatapp.ui.chat.chatActivity.ChatActivity.Companion.sImage
import com.a5universe.chatapp.model.chat.Messages
import com.a5universe.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MessagesAdapter(
    private val chatActivity: ChatActivity,
    private val messagesArrayList: ArrayList<Messages>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val ITEM_SEND = 1
    private val ITEM_RECEIVE = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_SEND -> {
                val view = LayoutInflater.from(chatActivity).inflate(R.layout.sender_layout_item, parent, false)
                SenderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(chatActivity).inflate(R.layout.receiver_layout_item, parent, false)
                ReceiverViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return messagesArrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages = messagesArrayList[position]

        when (holder) {
            is SenderViewHolder -> {
                holder.txtMessages.text = messages.message
                Picasso.get().load(sImage).into(holder.imageView)
            }
            is ReceiverViewHolder -> {
                holder.txtMessages.text = messages.message
                Picasso.get().load(rImage).into(holder.imageView)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val messages = messagesArrayList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid == messages.senderId) {
            ITEM_SEND
        } else {
            ITEM_RECEIVE
        }
    }

    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: CircleImageView
        var txtMessages: TextView

        init {
            imageView = itemView.findViewById(R.id.senderImg)
            txtMessages = itemView.findViewById(R.id.txtMessages)
        }
    }

    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: CircleImageView
        var txtMessages: TextView

        init {
            imageView = itemView.findViewById(R.id.receiverImg)
            txtMessages = itemView.findViewById(R.id.txtMessages)
        }
    }


}
