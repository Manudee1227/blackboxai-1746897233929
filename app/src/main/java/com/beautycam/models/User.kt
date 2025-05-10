package com.beautycam.models

data class User(
    val id: String = "",
    val username: String = "",
    val displayName: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isVerified: Boolean = false,
    val isPrivate: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "username" to username,
        "displayName" to displayName,
        "bio" to bio,
        "profileImageUrl" to profileImageUrl,
        "postsCount" to postsCount,
        "followersCount" to followersCount,
        "followingCount" to followingCount,
        "isVerified" to isVerified,
        "isPrivate" to isPrivate
    )
}
