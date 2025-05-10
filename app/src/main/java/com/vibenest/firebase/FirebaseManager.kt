package com.vibenest.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.vibenest.models.User
import com.vibenest.models.Post
import com.vibenest.models.Message
import com.vibenest.models.Chat

class FirebaseManager private constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance()

    companion object {
        @Volatile
        private var instance: FirebaseManager? = null

        fun getInstance(): FirebaseManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseManager().also { instance = it }
            }
        }
    }

    // Authentication Methods
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserOnlineStatus(true)
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun signUp(email: String, password: String, userData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.let { user ->
                    val completeUserData = userData.toMutableMap().apply {
                        put("id", user.uid)
                        put("email", email)
                        put("createdAt", System.currentTimeMillis())
                        put("isOnline", true)
                    }
                    createUserProfile(completeUserData) { success, error ->
                        if (success) {
                            callback(true, null)
                        } else {
                            user.delete() // Rollback user creation if profile creation fails
                            callback(false, error)
                        }
                    }
                } ?: callback(false, "User creation failed")
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun signOut(callback: (Boolean) -> Unit) {
        updateUserOnlineStatus(false) {
            auth.signOut()
            callback(true)
        }
    }

    fun resetPassword(email: String, callback: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    // User Profile Methods
    private fun createUserProfile(userData: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        db.collection("users")
            .document(userData["id"] as String)
            .set(userData)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    fun updateUserProfile(updates: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        getCurrentUser()?.let { user ->
            db.collection("users")
                .document(user.uid)
                .update(updates)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        } ?: callback(false, "User not logged in")
    }

    fun getUserProfile(userId: String, callback: (User?, String?) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    callback(User.fromMap(document.data as Map<String, Any>), null)
                } else {
                    callback(null, "User not found")
                }
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }
    }

    private fun updateUserOnlineStatus(online: Boolean, callback: (() -> Unit)? = null) {
        getCurrentUser()?.let { user ->
            db.collection("users")
                .document(user.uid)
                .update(
                    mapOf(
                        "isOnline" to online,
                        "lastLoginAt" to System.currentTimeMillis()
                    )
                )
                .addOnCompleteListener {
                    callback?.invoke()
                }
        } ?: callback?.invoke()
    }

    // FCM Token Methods
    fun getFCMToken(callback: (String?, String?) -> Unit) {
        messaging.token
            .addOnSuccessListener { token ->
                callback(token, null)
            }
            .addOnFailureListener { e ->
                callback(null, e.message)
            }
    }

    // Post Methods
    fun createPost(post: Post, callback: (Boolean, String?) -> Unit) {
        getCurrentUser()?.let { user ->
            val postId = db.collection("posts").document().id
            val postData = post.copy(id = postId, userId = user.uid).toMap()

            db.collection("posts")
                .document(postId)
                .set(postData)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        } ?: callback(false, "User not logged in")
    }

    // Message Methods
    fun sendMessage(message: Message, callback: (Boolean, String?) -> Unit) {
        getCurrentUser()?.let { user ->
            val messageId = db.collection("messages").document().id
            val messageData = message.copy(
                id = messageId,
                senderId = user.uid,
                createdAt = System.currentTimeMillis()
            ).toMap()

            db.collection("messages")
                .document(messageId)
                .set(messageData)
                .addOnSuccessListener {
                    updateChatLastMessage(message.chatId, messageData) { success, error ->
                        callback(success, error)
                    }
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        } ?: callback(false, "User not logged in")
    }

    private fun updateChatLastMessage(chatId: String, lastMessage: Map<String, Any>, callback: (Boolean, String?) -> Unit) {
        db.collection("chats")
            .document(chatId)
            .update(
                mapOf(
                    "lastMessage" to lastMessage,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    // Chat Methods
    fun createChat(chat: Chat, callback: (Boolean, String?) -> Unit) {
        getCurrentUser()?.let { user ->
            val chatId = chat.id.ifEmpty { db.collection("chats").document().id }
            val chatData = chat.copy(id = chatId).toMap()

            db.collection("chats")
                .document(chatId)
                .set(chatData)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, e.message)
                }
        } ?: callback(false, "User not logged in")
    }
}
