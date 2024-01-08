package com.a5universe.chatapp.model.connect

class FriendRequest{
    var requestId: String? = null
    var receiverUid: String? = null
    var senderUid: String? = null
    var senderName: String? = null
    var senderImage: String? = null
    var status: String? = null

    constructor(){}

    constructor(
        requestId: String?,
        receiverUid: String?,
        senderUid: String?,
        senderName: String?,
        senderImage: String?,
        status: String?
    ) {
        this.requestId = requestId
        this.receiverUid = receiverUid
        this.senderUid = senderUid
        this.senderName = senderName
        this.senderImage = senderImage
        this.status = status
    }


}


