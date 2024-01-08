package com.a5universe.chatapp.model.group

class GroupChat{

    var message: String? = null
    var senderId: String? = null
    var senderName: String? = null // New field for sender's name
    var senderImage: String? = null // New field for sender's image
    var messageId: String? = null
    var timestamp: String? = null

    constructor(){}
    constructor(message: String?,
                senderId: String?,
                senderName: String?, // New parameter for sender's name
                senderImage: String?, // New parameter for sender's image
                messageId: String?,
                timestamp: String?
    ) {
        this.message = message
        this.senderId = senderId
        this.senderName = senderName
        this.senderImage = senderImage
        this.messageId = messageId
        this.timestamp = timestamp
    }


}

