import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.connect.FriendRequest
import com.a5universe.chatapp.ui.connect.connectFragment.RequestFriendsFragment
import com.squareup.picasso.Picasso

class RequestFriendsAdapter(
    private val requestFriendsFragment: RequestFriendsFragment,
    private val usersArrayList: ArrayList<FriendRequest>,
    private val requestActionListener: OnRequestActionListener
) :
        RecyclerView.Adapter<RequestFriendsAdapter.ViewHolder>() {
    interface OnRequestActionListener {
        fun onAcceptFriendRequest(user: FriendRequest)
        fun onDeclineFriendRequest(user: FriendRequest)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestFriendsAdapter.ViewHolder {
        val view = LayoutInflater.from(requestFriendsFragment.requireContext())
                .inflate(R.layout.item_user_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestFriendsAdapter.ViewHolder, position: Int) {
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

        holder.acceptButton.setOnClickListener {
            requestActionListener.onAcceptFriendRequest(users)
        }
        holder.declineButton.setOnClickListener {
            requestActionListener.onDeclineFriendRequest(users)
        }

    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var senderImage: ImageView = itemView.findViewById(R.id.senderImage)
        var senderName: TextView = itemView.findViewById(R.id.senderName)
        var acceptButton: Button = itemView.findViewById(R.id.btnAccept)
        var declineButton: Button = itemView.findViewById(R.id.btnDecline)
    }
}