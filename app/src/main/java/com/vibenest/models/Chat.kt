package com.vibenest.models

data class Chat(
    val id: String = "",
    val participants: List<String> = emptyList(), // List of user IDs
    val participantNames: Map<String, String> = emptyMap(), // userId to name mapping
    val participantProfiles: Map<String, String> = emptyMap(), // userId to profile image URL mapping
    val lastMessage: Message? = null,
    val unreadCount: Map<String, Int> = emptyMap(), // userId to unread count mapping
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val typing: Map<String, Boolean> = emptyMap(), // userId to typing status mapping
    val muted: Map<String, Boolean> = emptyMap(), // userId to mute status mapping
    val pinned: Map<String, Boolean> = emptyMap() // userId to pin status mapping
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "participants" to participants,
            "participantNames" to participantNames,
            "participantProfiles" to participantProfiles,
            "lastMessage" to (lastMessage?.toMap() ?: emptyMap()),
            "unreadCount" to unreadCount,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "typing" to typing,
            "muted" to muted,
            "pinned" to pinned
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Chat {
            return Chat(
                id = map["id"] as? String ?: "",
                participants = (map["participants"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                participantNames = (map["participantNames"] as? Map<*, *>)?.mapNotNull {
                    it.key as? String to it.value as? String
                }?.toMap() ?: emptyMap(),
                participantProfiles = (map["participantProfiles"] as? Map<*, *>)?.mapNotNull {
                    it.key as? String to it.value as? String
                }?.toMap() ?: emptyMap(),
                lastMessage = (map["lastMessage"] as? Map<*, *>)?.let { messageMap ->
                    @Suppress("UNCHECKED_CAST")
                    Message.fromMap(messageMap as Map<String, Any>)
                },
                unreadCount = (map["unreadCount"] as? Map<*, *>)?.mapNotNull {
                    it.key as? String to (it.value as? Long)?.toInt() ?: 0
                }?.toMap() ?: emptyMap(),
                createdAt = map["createdAt"] as? Long ?: 0,
                updatedAt = map["updatedAt"] as? Long ?: 0,
                typing = (map["typing"] as? Map<*, *>)?.mapNotNull {
                    it.key as? String to it.value as? Boolean
                }?.toMap() ?: emptyMap(),
                muted = (map["muted"] as? Map<*, *>)?.mapNotNull {
                    it.key as? String to it.value as? Boolean
                }?.toMap() ?: emptyMap(),
                pinned = (map["pinned"] as? Map<*, *>)?.mapNotNull {
                    it.key as? String to it.value as? Boolean
                }?.toMap() ?: emptyMap()
            )
        }
    }

    fun getOtherParticipantId(currentUserId: String): String? {
        return participants.firstOrNull { it != currentUserId }
    }

    fun getOtherParticipantName(currentUserId: String): String {
        return getOtherParticipantId(currentUserId)?.let { participantNames[it] } ?: ""
    }

    fun getOtherParticipantProfile(currentUserId: String): String {
        return getOtherParticipantId(currentUserId)?.let { participantProfiles[it] } ?: ""
    }

    fun getUnreadCount(userId: String): Int {
        return unreadCount[userId] ?: 0
    }

    fun isTyping(userId: String): Boolean {
        return typing[userId] ?: false
    }

    fun isMuted(userId: String): Boolean {
        return muted[userId] ?: false
    }

    fun isPinned(userId: String): Boolean {
        return pinned[userId] ?: false
    }
}
