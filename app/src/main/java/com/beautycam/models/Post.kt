package com.beautycam.models

import java.util.Date

data class Post(
    val id: String = "",
    val userId: String = "",
    val caption: String = "",
    val imageUrls: List<String> = listOf(),
    val videoUrl: String? = null,
    val thumbnailUrl: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val timestamp: Date = Date(),
    val location: String? = null,
    val tags: List<String> = listOf(),
    val type: PostType = PostType.PHOTO
) {
    enum class PostType {
        PHOTO, VIDEO, MULTIPLE_PHOTOS
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "caption" to caption,
        "imageUrls" to imageUrls,
        "videoUrl" to videoUrl,
        "thumbnailUrl" to thumbnailUrl,
        "likesCount" to likesCount,
        "commentsCount" to commentsCount,
        "timestamp" to timestamp,
        "location" to location,
        "tags" to tags,
        "type" to type.name
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Post {
            return Post(
                id = map["id"] as? String ?: "",
                userId = map["userId"] as? String ?: "",
                caption = map["caption"] as? String ?: "",
                imageUrls = (map["imageUrls"] as? List<*>)?.filterIsInstance<String>() ?: listOf(),
                videoUrl = map["videoUrl"] as? String,
                thumbnailUrl = map["thumbnailUrl"] as? String,
                likesCount = (map["likesCount"] as? Number)?.toInt() ?: 0,
                commentsCount = (map["commentsCount"] as? Number)?.toInt() ?: 0,
                timestamp = (map["timestamp"] as? Date) ?: Date(),
                location = map["location"] as? String,
                tags = (map["tags"] as? List<*>)?.filterIsInstance<String>() ?: listOf(),
                type = PostType.valueOf(map["type"] as? String ?: PostType.PHOTO.name)
            )
        }
    }
}
