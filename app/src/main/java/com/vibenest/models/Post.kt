package com.vibenest.models

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileUrl: String = "",
    val mediaUrl: String = "",
    val mediaType: MediaType = MediaType.IMAGE,
    val caption: String = "",
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val createdAt: Long = 0,
    val location: String = "",
    val hashtags: List<String> = emptyList(),
    val mentions: List<String> = emptyList(),
    val likedBy: List<String> = emptyList()
) {
    enum class MediaType {
        IMAGE, VIDEO, REEL
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "userName" to userName,
            "userProfileUrl" to userProfileUrl,
            "mediaUrl" to mediaUrl,
            "mediaType" to mediaType.name,
            "caption" to caption,
            "likes" to likes,
            "comments" to comments,
            "shares" to shares,
            "createdAt" to createdAt,
            "location" to location,
            "hashtags" to hashtags,
            "mentions" to mentions,
            "likedBy" to likedBy
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): Post {
            return Post(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                userProfileUrl = map["userProfileUrl"] as? String ?: "",
                mediaUrl = map["mediaUrl"] as? String ?: "",
                mediaType = try {
                    MediaType.valueOf(map["mediaType"] as String)
                } catch (e: Exception) {
                    MediaType.IMAGE
                },
                caption = map["caption"] as? String ?: "",
                likes = (map["likes"] as? Long)?.toInt() ?: 0,
                comments = (map["comments"] as? Long)?.toInt() ?: 0,
                shares = (map["shares"] as? Long)?.toInt() ?: 0,
                createdAt = map["createdAt"] as? Long ?: 0,
                location = map["location"] as? String ?: "",
                hashtags = (map["hashtags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                mentions = (map["mentions"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                likedBy = (map["likedBy"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            )
        }
    }
}
