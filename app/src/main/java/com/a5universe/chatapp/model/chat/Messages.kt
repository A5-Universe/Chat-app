package com.a5universe.chatapp.model.chat

class Messages {
    var message: String? = null
    var senderId: String? = null
    var timeStamp: Long = 0

    constructor() {}
    constructor(
        message: String?,
        senderId: String?,
        timeStamp: Long
    ) {
        this.message = message
        this.senderId = senderId
        this.timeStamp = timeStamp
    }
}