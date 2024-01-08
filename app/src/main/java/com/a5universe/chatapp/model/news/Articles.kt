package com.a5universe.chatapp.model.news

class Articles{
    var title: String? = null
    var description: String? = null
    var urlToImage: String? = null
    var url: String? = null
    var content: String? = null

    constructor(){}
    constructor(
        title: String?,
        description: String?,
        urlToImage: String?,
        url: String?,
        content: String?
    ) {
        this.title = title
        this.description = description
        this.urlToImage = urlToImage
        this.url = url
        this.content = content
    }


}

