package com.a5universe.chatapp.ui.news.newsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.news.CategoryRVModal
import com.squareup.picasso.Picasso

class CategoryRVAdapter(
    private val categoryRVModals: ArrayList<CategoryRVModal>,
    private val context: Context,
    private val categoryClickInterface: CategoryClickInterface
) : RecyclerView.Adapter<CategoryRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categories_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryRVModal = categoryRVModals[position]
        holder.categoryTV.text = categoryRVModal.category
        Picasso.get().load(categoryRVModal.categoryImageUrl).into(holder.categoryIV)
        holder.itemView.setOnClickListener {
            categoryClickInterface.onCategoryClick(position)
        }
    }

    override fun getItemCount(): Int {
        return categoryRVModals.size
    }

    interface CategoryClickInterface {
        fun onCategoryClick(position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTV: TextView = itemView.findViewById(R.id.idTVCategory)
        val categoryIV: ImageView = itemView.findViewById(R.id.idIVCategory)
    }
}