package com.example.ui.notes

import com.example.data.model.Note

enum class NoteFilter {
  ALL,
  PINNED,
  FAVORITES
}

enum class NoteSort {
  MODIFIED_DESC,
  MODIFIED_ASC,
  CREATED_DESC,
  TITLE_ASC,
  TITLE_DESC
}

data class NotesUiState(
  val notes: List<Note> = emptyList(),
  val totalCount: Int = 0,
  val pinnedCount: Int = 0,
  val favoriteCount: Int = 0,
  val searchQuery: String = "",
  val selectedFilter: NoteFilter = NoteFilter.ALL,
  val sortOption: NoteSort = NoteSort.MODIFIED_DESC,
  val isLoading: Boolean = true
)
