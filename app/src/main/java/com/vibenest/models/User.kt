package com.vibenest.models

data class User(
    val uid: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String = "",
    val profileImage: String = "",
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "username" to username,
        "email" to email,
        "bio" to bio,
        "profileImage" to profileImage,
        "postsCount" to postsCount,
        "followersCount" to followersCount,
        "followingCount" to followingCount,
        "isVerified" to isVerified,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): User = User(
            uid = map["uid"] as? String ?: "",
            name = map["name"] as? String ?: "",
            username = map["username"] as? String ?: "",
            email = map["email"] as? String ?: "",
            bio = map["bio"] as? String ?: "",
            profileImage = map["profileImage"] as? String ?: "",
            postsCount = (map["postsCount"] as? Long)?.toInt() ?: 0,
            followersCount = (map["followersCount"] as? Long)?.toInt() ?: 0,
            followingCount = (map["followingCount"] as? Long)?.toInt() ?: 0,
            isVerified = map["isVerified"] as? Boolean ?: false,
            createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis(),
            updatedAt = map["updatedAt"] as? Long ?: System.currentTimeMillis()
        )
    }
}
