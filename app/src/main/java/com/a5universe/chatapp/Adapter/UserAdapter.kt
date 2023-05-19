package com.a5universe.chatapp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.Activity.ChatActivity
import com.a5universe.chatapp.Fragment.ChatFragment
import com.a5universe.chatapp.Model.Users
import com.a5universe.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class UserAdapter (private val chatFragment: ChatFragment,
                   private val usersArrayList: ArrayList<Users>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(chatFragment.requireContext())
            .inflate(R.layout.item_user_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users = usersArrayList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid == users.uid) {
            holder.itemView.visibility = View.GONE // only visible gone
            // code for your profile remove for recycler view list...

        }

        holder.userName.text = users.name
        Picasso.get().load(users.imgUri).placeholder(R.drawable.sample_image).into(holder.userImage)

//         for chat activity
            holder.itemView.setOnClickListener {
            val intent = Intent(chatFragment.requireContext(), ChatActivity::class.java)
            intent.putExtra("name", users.name)
            intent.putExtra("ReceiverImage", users.imgUri)
            intent.putExtra("uid", users.uid)
            chatFragment.startActivity(intent)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImage: ImageView = itemView.findViewById(R.id.userImage)
        var userName: TextView = itemView.findViewById(R.id.userName)

    }


}




