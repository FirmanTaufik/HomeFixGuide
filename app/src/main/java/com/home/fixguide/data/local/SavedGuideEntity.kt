package com.home.fixguide.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_guides")
data class SavedGuideEntity(
    @PrimaryKey
    val id: String, // Menggunakan URL sebagai ID unik
    val title: String,
    val imageUrl: String = "",
    val url: String,
    val categoryType: String = "guide", // "guide", "device", atau "post"
    val savedAt: Long = System.currentTimeMillis()
)
