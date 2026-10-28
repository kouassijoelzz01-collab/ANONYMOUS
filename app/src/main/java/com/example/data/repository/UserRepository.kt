package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.UserProfile
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val db: FirebaseFirestore
) {
    companion object {
        private const val TAG = "UserRepository"
        private const val USERS_COLLECTION = "users"
    }

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun observeProfile(uid: String): Flow<UserProfile?> {
        return db.collection(USERS_COLLECTION).document(uid)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
    }

    suspend fun getProfile(uid: String): UserProfile? {
        val snapshot = db.collection(USERS_COLLECTION).document(uid).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } else {
            null
        }
    }

    suspend fun isPseudoAvailable(pseudo: String, currentUid: String): Boolean {
        val trimmed = pseudo.trim()
        if (trimmed.length < 3) return false
        val snapshot = db.collection(USERS_COLLECTION)
            .whereEqualTo("pseudo", trimmed)
            .get()
            .await()
        return snapshot.documents.all { it.id == currentUid }
    }

    suspend fun createOrUpdateProfile(
        uid: String,
        email: String,
        pseudo: String,
        avatarId: String
    ): Result<UserProfile> {
        return try {
            val docRef = db.collection(USERS_COLLECTION).document(uid)
            val existing = docRef.get().await()

            val profile = if (existing.exists()) {
                val current = existing.toObject(UserProfile::class.java)
                UserProfile(
                    uid = uid,
                    email = email.ifBlank { current?.email ?: "" },
                    pseudo = pseudo.trim(),
                    avatarId = avatarId,
                    wins = current?.wins ?: 0L,
                    losses = current?.losses ?: 0L,
                    gamesPlayed = current?.gamesPlayed ?: 0L,
                    createdAt = current?.createdAt
                )
            } else {
                UserProfile(
                    uid = uid,
                    email = email,
                    pseudo = pseudo.trim(),
                    avatarId = avatarId,
                    wins = 0L,
                    losses = 0L,
                    gamesPlayed = 0L
                )
            }

            val data = mapOf(
                "uid" to uid,
                "email" to profile.email,
                "pseudo" to profile.pseudo,
                "avatarId" to profile.avatarId,
                "wins" to profile.wins,
                "losses" to profile.losses,
                "gamesPlayed" to profile.gamesPlayed,
                "updatedAt" to FieldValue.serverTimestamp(),
                "createdAt" to (profile.createdAt ?: FieldValue.serverTimestamp())
            )
            docRef.set(data).await()
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save profile for uid $uid", e)
            Result.failure(e)
        }
    }

    suspend fun updateAvatar(uid: String, avatarId: String): Result<Unit> {
        return try {
            db.collection(USERS_COLLECTION).document(uid)
                .update(
                    mapOf(
                        "avatarId" to avatarId,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update avatar for uid $uid", e)
            Result.failure(e)
        }
    }

    suspend fun searchUsersByPseudo(query: String, currentUid: String): List<UserProfile> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .whereGreaterThanOrEqualTo("pseudo", trimmed)
                .whereLessThanOrEqualTo("pseudo", trimmed + "\uf8ff")
                .limit(20)
                .get()
                .await()

            snapshot.toObjects(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                .filter { it.uid != currentUid }
        } catch (e: Exception) {
            Log.e(TAG, "Failed searching users", e)
            emptyList()
        }
    }
}
