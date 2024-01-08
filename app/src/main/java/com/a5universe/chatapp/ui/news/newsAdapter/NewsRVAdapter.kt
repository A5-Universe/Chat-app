package com.a5universe.chatapp.ui.news.newsAdapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.model.news.Articles
import com.a5universe.chatapp.R
import com.a5universe.chatapp.ui.news.newsFragment.NewsDetailFragment
import com.squareup.picasso.Picasso


class NewsRVAdapter(
    private val articlesArrayList: ArrayList<Articles>,
    private val context: Context
) : RecyclerView.Adapter<NewsRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val articles = articlesArrayList[position]
        holder.subTitleTV.text = articles.description
        holder.titleTV.text = articles.title
        Picasso.get().load(articles.urlToImage).into(holder.newsIV)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("title", articles.title)
                putString("content", articles.content)
                putString("desc", articles.description)
                putString("image", articles.urlToImage)
                putString("url", articles.url)
            }

            val detailFragment = NewsDetailFragment()
            detailFragment.arguments = bundle

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, detailFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

    }

    override fun getItemCount(): Int {
        return articlesArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.idTVNewsHeading)
        val subTitleTV: TextView = itemView.findViewById(R.id.idTVSubTitle)
        val newsIV: ImageView = itemView.findViewById(R.id.idIVNews)
    }
}