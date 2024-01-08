package com.a5universe.chatapp.model.group

class GroupDetails{
    var groupId: String? = null
    var groupName: String? = null
    var groupImage: String? = null
    var adminName: String? = null
    var adminUid: String? = null
    var members: Map<String?, Boolean>? = null
    var groupChat: Map<String, GroupChat>? = null

    constructor(){}
    constructor(
        groupId: String?,
        groupName: String?,
        groupImage: String?,
        adminName: String?,
        adminUid: String?,
        members: Map<String?, Boolean>?,
        groupChat: Map<String, GroupChat>?
    ) {
        this.groupId = groupId
        this.groupName = groupName
        this.groupImage = groupImage
        this.adminName = adminName
        this.adminUid = adminUid
        this.members = members
        this.groupChat = groupChat
    }


}

