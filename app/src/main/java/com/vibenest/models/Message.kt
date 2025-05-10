
package com.vibenest.models

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderProfileUrl: String = "",
    val receiverId: String = "",
    val content: String = "",
    val mediaUrl: String = "",
    val mediaType: MediaType = MediaType.NONE,
    val createdAt: Long = 0,
    val readAt: Long = 0,
    val status: MessageStatus = MessageStatus.SENT,
    val replyTo: String = "", // ID of the message being replied to
    val reactions: Map<String, String> = emptyMap() // userId to reaction emoji mapping
) {
    enum class MediaType {
        NONE, IMAGE, VIDEO, AUDIO, FILE
    }

    enum class MessageStatus {
        SENDING, SENT, DELIVERED, READ, FAILED
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "chatId" to chatId,
            "senderId" to senderId,
            "senderName" to senderName,
            "senderProfileUrl" to senderProfileUrl,
            "receiverId" to receiverId,
            "content" to content,
            "mediaUrl" to mediaUrl,
            "mediaType" to mediaType.name,
            "createdAt" to createdAt,
            "readAt" to readAt,
            "status" to status.name,
            "replyTo" to replyTo,
            "reactions" to reactions
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Message {
            return Message(
                id = map["id"] as? String ?: "",
                chatId = map["chatId"] as? String ?: "",
                senderId = map["senderId"] as? String ?: "",
                senderName = map["senderName"] as? String ?: "",
                senderProfileUrl = map["senderProfileUrl"] as? String ?: "",
                receiverId = map["receiverId"] as? String ?: "",
                content = map["content"] as? String ?: "",
                mediaUrl = map["mediaUrl"] as? String ?: "",
                mediaType = try {
                    MediaType.valueOf(map["mediaType"] as String)
                } catch (e: Exception) {
                    MediaType.NONE
                },
                createdAt = map["createdAt"] as? Long ?: 0,
                readAt = map["readAt"] as? Long ?: 0,
                status = try {
                    MessageStatus.valueOf(map["status"] as String)
                } catch (e: Exception) {
                    MessageStatus.SENT
                },
                replyTo = map["replyTo"] as? String ?: "",
                reactions = (map["reactions"] as? Map<*, *>)?.mapNotNull {
                    it.key as? String to it.value as? String
                }?.toMap() ?: emptyMap()
            )
        }

        fun createChatId(userId1: String, userId2: String): String {
            // Create a consistent chat ID regardless of the order of user IDs
            return if (userId1 < userId2) {
                "${userId1}_${userId2}"
            } else {
                "${userId2}_${userId1}"
            }
        }
    }
}
