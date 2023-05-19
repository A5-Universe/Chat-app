package com.a5universe.chatapp.Model

class Users {
    var uid: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var imgUri: String? = null

    constructor() {}
    constructor(
        uid: String?,
        name: String?,
        email: String?,
        password: String?,
        imgUri: String?

    ) {
        this.uid = uid
        this.name = name
        this.email = email
        this.imgUri = imgUri
        this.password = password
    }
}