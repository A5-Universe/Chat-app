package com.a5universe.chatapp.model.news

class NewsModal{
    var totalResults: Int? = null
    var status: String? = null
    var articles: ArrayList<Articles> = ArrayList()
    constructor()
    constructor(
        totalResults: Int,
        status: String,
        articles: ArrayList<Articles>
    ) {
        this.totalResults = totalResults
        this.status = status
        this.articles = articles
    }
}

