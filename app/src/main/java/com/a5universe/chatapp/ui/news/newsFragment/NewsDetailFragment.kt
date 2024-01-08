package com.a5universe.chatapp.ui.news.newsFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.a5universe.chatapp.R

class NewsDetailFragment : Fragment() {

    private lateinit var title: String
    private lateinit var desc: String
    private lateinit var content: String
    private lateinit var imageURL: String
    private lateinit var url: String

    private lateinit var titleTV: TextView
    private lateinit var subDescTV: TextView
    private lateinit var contentTV: TextView
    private lateinit var newsIV: ImageView
    private lateinit var readNewsBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_detail, container, false)

        // Retrieve data from arguments
        title = requireArguments().getString("title") ?: ""
        content = requireArguments().getString("content") ?: ""

        //test
        Log.d("TAG", "onCreateView:$content ")
        desc = requireArguments().getString("desc") ?: ""
        imageURL = requireArguments().getString("image") ?: ""
        url = requireArguments().getString("url") ?: ""

        // Initialize UI elements
        titleTV = view.findViewById(R.id.idTVTitle)
        subDescTV = view.findViewById(R.id.idTVSubDesc)
        contentTV = view.findViewById(R.id.idTVContent)
        newsIV = view.findViewById(R.id.idIVNews)
        readNewsBtn = view.findViewById(R.id.idBtnReadNews)

        titleTV.text = title
        subDescTV.text = desc
        contentTV.text = content
        Picasso.get().load(imageURL).into(newsIV)

        // Click listener for more details to go through the news website
        readNewsBtn.setOnClickListener {
            openNewsWebsite()
        }

        // Set up the back press handling
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goBackToPreviousFragment()
            }
        })

        return view
    }

    private fun openNewsWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun goBackToPreviousFragment() {
        // If you want to navigate back to the previous fragment
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack()
    }
}
