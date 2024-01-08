package com.a5universe.chatapp.ui.connect.connectAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.authentication.User
import com.a5universe.chatapp.ui.connect.connectFragment.FindFriendsFragment
import com.squareup.picasso.Picasso

class FindFriendsAdapter(
    private val context: Context,
    private var usersArrayList: MutableList<User>,
    private val addFriendClickListener: OnAddFriendClickListener?
) :
    RecyclerView.Adapter<FindFriendsAdapter.ViewHolder>() {

//        test

    private var searchQuery: String = ""

    init {
        setHasStableIds(true)
    }
//        test

    interface OnAddFriendClickListener {
    fun onAddFriendClick(user: User)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_user_find_friends, parent, false)
        return ViewHolder(view)
    }

//    test

    override fun getItemId(position: Int): Long {
        // Ensure stable IDs for proper animation
        return usersArrayList[position].uid.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val users = usersArrayList[position]
        holder.userName.text = users.name ?: "Empty Name"

        // Load user image using Picasso or any other image loading library
        val imgUri = users.imgUri
        if (imgUri?.isNotEmpty() == true) {
            Picasso.get().load(imgUri).placeholder(R.drawable.user1).into(holder.userImage)
        } else {
            val placeholderImageUrl =
                "https://firebasestorage.googleapis.com/v0/b/a5-chat-app.appspot.com/o/user.png?alt=media&token=ab8059c6-a92e-4b63-b459-4d76e8e7566c"
            Picasso.get().load(placeholderImageUrl).into(holder.userImage)
        }
        
        holder.btnAdd.setOnClickListener {
            val user = usersArrayList[position]
            addFriendClickListener?.onAddFriendClick(user)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImage: ImageView = itemView.findViewById(R.id.userImage)
        var userName: TextView = itemView.findViewById(R.id.userName)
        val btnAdd: Button = itemView.findViewById(R.id.btnAdd)


    }
    fun filter(query: String) {
        searchQuery = query.trim()
        usersArrayList = if (searchQuery.isNotEmpty()) {
            val filteredList = usersArrayList.filter { user ->
                user.name?.contains(searchQuery, ignoreCase = true) == true
            }.toMutableList()
            val remainingList = usersArrayList.filterNot { user ->
                user.name?.contains(searchQuery, ignoreCase = true) == true
            }.toMutableList()

            filteredList.addAll(remainingList)
            filteredList
        } else {
            ArrayList(usersArrayList) // Create a new ArrayList to avoid modifying the original list
        }
        notifyDataSetChanged()
    }

}