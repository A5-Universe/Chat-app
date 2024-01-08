package com.a5universe.chatapp.model.news

class CategoryRVModal{
    var category: String? = null
    var categoryImageUrl: String? = null

    constructor()
    constructor(
        category: String?,
        categoryImageUrl: String?
    ) {
        this.category = category
        this.categoryImageUrl = categoryImageUrl
    }

}

