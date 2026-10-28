package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val uid: String = "",
    val email: String = "",
    val pseudo: String = "",
    val avatarId: String = "cyber_ninja",
    val wins: Long = 0L,
    val losses: Long = 0L,
    val gamesPlayed: Long = 0L,
    val isGuest: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val winRate: Int
        get() = if (gamesPlayed > 0) ((wins.toDouble() / gamesPlayed) * 100).toInt() else 0
}

data class Avatar(
    val id: String,
    val name: String,
    val emoji: String,
    val role: String,
    val hexColor: Long
)

object AvatarCatalog {
    val avatars = listOf(
        Avatar("cyber_ninja", "Cyber Ninja", "🥷", "Spécialiste tactique", 0xFFA855F7),
        Avatar("neon_cat", "Neon Neko", "🐱", "Agile & furtif", 0xFF06B6D4),
        Avatar("glitch_bot", "Glitch Bot", "🤖", "Calculateur IA", 0xFF10B981),
        Avatar("pixel_hero", "Pixel Hero", "🦸", "Champion 8-bit", 0xFFEC4899),
        Avatar("cosmic_mage", "Cosmic Mage", "🧙", "Maître des arcanes", 0xFFF59E0B),
        Avatar("arcade_king", "Arcade King", "👑", "Légende rétro", 0xFFEAB308),
        Avatar("shadow_fox", "Shadow Fox", "🦊", "Rusé & rapide", 0xFFF97316),
        Avatar("plasma_dragon", "Plasma Dragon", "🐲", "Force brute", 0xFFEF4444)
    )

    fun getAvatar(id: String): Avatar {
        return avatars.find { it.id == id } ?: avatars.first()
    }
}
