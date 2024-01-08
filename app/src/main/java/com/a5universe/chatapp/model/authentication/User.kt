package com.a5universe.chatapp.model.authentication


class User {
    var uid: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var imgUri: String? = null
    var status: String? = null // New field for user status

    constructor() {}
    constructor(
        uid: String?,
        name: String?,
        email: String?,
        password: String?,
        imgUri: String?,
        status: String? // New parameter for status

    ) {
        this.uid = uid
        this.name = name
        this.email = email
        this.imgUri = imgUri
        this.password = password
        this.status = status // add New
    }
}