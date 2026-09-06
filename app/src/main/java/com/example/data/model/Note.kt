package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a notepad note stored in Room database.
 */
@Entity(tableName = "notes")
data class Note(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,

  @ColumnInfo(name = "title")
  val title: String = "",

  @ColumnInfo(name = "content")
  val content: String = "",

  @ColumnInfo(name = "created_at")
  val createdAt: Long = System.currentTimeMillis(),

  @ColumnInfo(name = "updated_at")
  val updatedAt: Long = System.currentTimeMillis(),

  @ColumnInfo(name = "is_pinned")
  val isPinned: Boolean = false,

  @ColumnInfo(name = "is_favorite")
  val isFavorite: Boolean = false,

  @ColumnInfo(name = "color_tag")
  val colorTag: Long = 0L
) {
  val wordCount: Int
    get() {
      val trimmed = content.trim()
      if (trimmed.isEmpty()) return 0
      return trimmed.split("\\s+".toRegex()).size
    }

  val characterCount: Int
    get() = content.length
}
