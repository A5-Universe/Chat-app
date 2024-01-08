package com.a5universe.chatapp.ui.news.newsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a5universe.chatapp.R
import com.a5universe.chatapp.model.news.Articles
import com.a5universe.chatapp.model.news.CategoryRVModal
import com.a5universe.chatapp.ui.news.newsAdapter.CategoryRVAdapter
import com.a5universe.chatapp.ui.news.newsAdapter.NewsRVAdapter
import com.a5universe.chatapp.model.news.NewsModal
import com.a5universe.chatapp.model.news.network.RetrofitApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeNewsFragment : Fragment(), CategoryRVAdapter.CategoryClickInterface {

    private lateinit var newsRV: RecyclerView
    private lateinit var categoryRV: RecyclerView
    private lateinit var loadingPB: ProgressBar
    private lateinit var articlesArrayList: ArrayList<Articles>
    private lateinit var categoryRVModalArrayList: ArrayList<CategoryRVModal>
    private lateinit var categoryRVAdapter: CategoryRVAdapter
    private lateinit var newsRVAdapter: NewsRVAdapter
    //news_api.org api key - 5a61ff1b816447d2b43b79a4072312f5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsRV = view.findViewById(R.id.idRvNews)
        categoryRV = view.findViewById(R.id.idRvCategories)
        loadingPB = view.findViewById(R.id.idProgress)

        // set category recycler view data
        categoryRVModalArrayList = ArrayList()
        categoryRVAdapter = CategoryRVAdapter(categoryRVModalArrayList, requireContext(), this)
        categoryRV.adapter = categoryRVAdapter

        // set news recycler view data
        articlesArrayList = ArrayList()
        newsRVAdapter = NewsRVAdapter(articlesArrayList, requireContext()) //test for add this
        newsRV.layoutManager = LinearLayoutManager(requireContext())
        newsRV.adapter = newsRVAdapter

        // call
        getCategories()
        getNews("All")
    }


    private fun getCategories() {
        categoryRVModalArrayList.add(CategoryRVModal("All", "https://images.unsplash.com/photo-1569982175971-d92b01cf8694?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=735&q=80"))
        categoryRVModalArrayList.add(CategoryRVModal("Technology", "https://images.unsplash.com/photo-1488590528505-98d2b5aba04b?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1170&q=80"))
        categoryRVModalArrayList.add(CategoryRVModal("Science", "https://plus.unsplash.com/premium_photo-1676750395664-3ac0783ae2c2?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=687&q=80"))
        categoryRVModalArrayList.add(CategoryRVModal("Sports", "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1170&q=80"))
        categoryRVModalArrayList.add(CategoryRVModal("General", "https://images.unsplash.com/photo-1512314889357-e157c22f938d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1171&q=80"))
        categoryRVModalArrayList.add(CategoryRVModal("Business", "https://plus.unsplash.com/premium_photo-1661764256397-af154e87b1b3?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1170&q=80"))
        categoryRVModalArrayList.add(CategoryRVModal("Entertainment", "https://images.unsplash.com/photo-1598899134739-24c46f58b8c0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1156&q=80"))
        categoryRVModalArrayList.add(CategoryRVModal("Health", "https://images.unsplash.com/photo-1543362906-acfc16c67564?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8aGVhbHRofGVufDB8fDB8fHww&auto=format&fit=crop&w=600&q=60"))


        categoryRVAdapter.notifyDataSetChanged()
    }

    private fun getNews(category: String) {
        loadingPB.visibility = View.VISIBLE
        //clear previous news for recyclerview
        articlesArrayList.clear()

        //url
        val categoryURL = "https://newsapi.org/v2/top-headlines?country=in&category=$category&apiKey=5a61ff1b816447d2b43b79a4072312f5"
        val url = "https://newsapi.org/v2/top-headlines?country=in&exludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apiKey=5a61ff1b816447d2b43b79a4072312f5"
        val BASE_URL = "https://newsapi.org/"

        // 1st step build url
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //call url
        val retrofitApi = retrofit.create(RetrofitApi::class.java)
        val call: Call<NewsModal> = if (category == "All") {
            retrofitApi.getAllNews(url)
        } else {
            retrofitApi.getNewsByCategory(categoryURL)
        }

        //response data
        call.enqueue(object : Callback<NewsModal> {
            override fun onResponse(call: Call<NewsModal>, response: Response<NewsModal>) {

                // The onResponse method is called when the network request is successful
                val newsModal = response.body() // Parse the response body into a NewsModal object
                loadingPB.visibility = View.GONE

                val articles = newsModal?.articles
                articles?.let {
                    for (i in it.indices) {
                        articlesArrayList.add(
                            Articles(
                                it[i].title,
                                it[i].description,
                                it[i].urlToImage,
                                it[i].url,
                                it[i].content
                            )
                        )
                    }
                }

                newsRVAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<NewsModal>, t: Throwable) {
                Toast.makeText(requireContext(), "Fail to get news", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCategoryClick(position: Int) {
        val category = categoryRVModalArrayList[position].category
        category?.let {
            getNews(it)
        }
    }

}