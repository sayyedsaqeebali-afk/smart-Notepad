package com.example.ui.notes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.NoteDatabase
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import com.example.data.repository.NoteRepositoryImpl
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
  private val repository: NoteRepository
) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  private val _selectedFilter = MutableStateFlow(NoteFilter.ALL)
  val selectedFilter = _selectedFilter.asStateFlow()

  private val _sortOption = MutableStateFlow(NoteSort.MODIFIED_DESC)
  val sortOption = _sortOption.asStateFlow()

  private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
  val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

  private var lastDeletedNote: Note? = null

  val uiState: StateFlow<NotesUiState> = combine(
    repository.getAllNotes(),
    _searchQuery,
    _selectedFilter,
    _sortOption
  ) { allNotes, query, filter, sort ->
    val trimmedQuery = query.trim()

    // 1. Filter by search query
    val searched = if (trimmedQuery.isEmpty()) {
      allNotes
    } else {
      allNotes.filter {
        it.title.contains(trimmedQuery, ignoreCase = true) ||
            it.content.contains(trimmedQuery, ignoreCase = true)
      }
    }

    // 2. Filter by tab/filter
    val filtered = when (filter) {
      NoteFilter.ALL -> searched
      NoteFilter.PINNED -> searched.filter { it.isPinned }
      NoteFilter.FAVORITES -> searched.filter { it.isFavorite }
    }

    // 3. Sort notes (Pinned notes always on top, then sorted according to selected sortOption)
    val sorted = filtered.sortedWith(
      compareByDescending<Note> { it.isPinned }
        .then(
          when (sort) {
            NoteSort.MODIFIED_DESC -> compareByDescending { it.updatedAt }
            NoteSort.MODIFIED_ASC -> compareBy { it.updatedAt }
            NoteSort.CREATED_DESC -> compareByDescending { it.createdAt }
            NoteSort.TITLE_ASC -> compareBy { it.title.lowercase() }
            NoteSort.TITLE_DESC -> compareByDescending { it.title.lowercase() }
          }
        )
    )

    NotesUiState(
      notes = sorted,
      totalCount = allNotes.size,
      pinnedCount = allNotes.count { it.isPinned },
      favoriteCount = allNotes.count { it.isFavorite },
      searchQuery = query,
      selectedFilter = filter,
      sortOption = sort,
      isLoading = false
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = NotesUiState(isLoading = true)
  )

  fun onSearchQueryChanged(newQuery: String) {
    _searchQuery.value = newQuery
  }

  fun onFilterSelected(filter: NoteFilter) {
    _selectedFilter.value = filter
  }

  fun onSortOptionSelected(sort: NoteSort) {
    _sortOption.value = sort
  }

  fun setThemeMode(mode: ThemeMode) {
    _themeMode.value = mode
  }

  fun togglePin(note: Note) {
    viewModelScope.launch {
      repository.togglePin(note.id, !note.isPinned)
    }
  }

  fun toggleFavorite(note: Note) {
    viewModelScope.launch {
      repository.toggleFavorite(note.id, !note.isFavorite)
    }
  }

  fun deleteNote(note: Note) {
    lastDeletedNote = note
    viewModelScope.launch {
      repository.delete(note)
    }
  }

  fun restoreLastDeletedNote() {
    val note = lastDeletedNote ?: return
    viewModelScope.launch {
      repository.insertOrUpdate(note)
      lastDeletedNote = null
    }
  }

  companion object {
    fun provideFactory(context: Context): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val database = NoteDatabase.getDatabase(context.applicationContext)
          val repository = NoteRepositoryImpl(database.noteDao())
          return NotesViewModel(repository) as T
        }
      }
  }
}
