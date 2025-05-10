package com.beautycam.models

import java.util.Date

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Date = Date(),
    val isRead: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null
) {
    enum class MessageType {
        TEXT,
        IMAGE,
        VIDEO,
        AUDIO
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "senderId" to senderId,
        "receiverId" to receiverId,
        "content" to content,
        "timestamp" to timestamp,
        "isRead" to isRead,
        "type" to type.name,
        "mediaUrl" to mediaUrl
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Message {
            return Message(
                id = map["id"] as? String ?: "",
                senderId = map["senderId"] as? String ?: "",
                receiverId = map["receiverId"] as? String ?: "",
                content = map["content"] as? String ?: "",
                timestamp = (map["timestamp"] as? Date) ?: Date(),
                isRead = map["isRead"] as? Boolean ?: false,
                type = MessageType.valueOf(map["type"] as? String ?: MessageType.TEXT.name),
                mediaUrl = map["mediaUrl"] as? String
            )
        }
    }
}
