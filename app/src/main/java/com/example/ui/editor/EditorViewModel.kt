package com.example.ui.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.NoteDatabase
import com.example.data.model.Note
import com.example.data.repository.NoteRepository
import com.example.data.repository.NoteRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorUiState(
  val noteId: Long = 0L,
  val title: String = "",
  val content: String = "",
  val isPinned: Boolean = false,
  val isFavorite: Boolean = false,
  val colorTag: Long = 0L,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val isSaving: Boolean = false,
  val isSaved: Boolean = true,
  val wordCount: Int = 0,
  val charCount: Int = 0,
  val isLoaded: Boolean = false
)

class EditorViewModel(
  private val repository: NoteRepository,
  private val initialNoteId: Long
) : ViewModel() {

  private val _uiState = MutableStateFlow(EditorUiState(noteId = initialNoteId))
  val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

  private var autoSaveJob: Job? = null
  private var currentNoteId: Long = initialNoteId

  init {
    loadNote()
  }

  private fun loadNote() {
    if (initialNoteId > 0L) {
      viewModelScope.launch {
        val existingNote = repository.getNoteByIdDirect(initialNoteId)
        if (existingNote != null) {
          currentNoteId = existingNote.id
          _uiState.update {
            it.copy(
              noteId = existingNote.id,
              title = existingNote.title,
              content = existingNote.content,
              isPinned = existingNote.isPinned,
              isFavorite = existingNote.isFavorite,
              colorTag = existingNote.colorTag,
              createdAt = existingNote.createdAt,
              updatedAt = existingNote.updatedAt,
              wordCount = existingNote.wordCount,
              charCount = existingNote.characterCount,
              isLoaded = true,
              isSaved = true
            )
          }
        } else {
          _uiState.update { it.copy(isLoaded = true) }
        }
      }
    } else {
      _uiState.update { it.copy(isLoaded = true) }
    }
  }

  fun onTitleChanged(newTitle: String) {
    val current = _uiState.value
    _uiState.update {
      it.copy(
        title = newTitle,
        isSaved = false
      )
    }
    triggerAutoSave()
  }

  fun onContentChanged(newContent: String) {
    val words = calculateWords(newContent)
    _uiState.update {
      it.copy(
        content = newContent,
        wordCount = words,
        charCount = newContent.length,
        isSaved = false
      )
    }
    triggerAutoSave()
  }

  fun togglePin() {
    val currentPinned = _uiState.value.isPinned
    _uiState.update { it.copy(isPinned = !currentPinned, isSaved = false) }
    saveImmediately()
  }

  fun toggleFavorite() {
    val currentFavorite = _uiState.value.isFavorite
    _uiState.update { it.copy(isFavorite = !currentFavorite, isSaved = false) }
    saveImmediately()
  }

  fun setColorTag(colorTag: Long) {
    _uiState.update { it.copy(colorTag = colorTag, isSaved = false) }
    saveImmediately()
  }

  private fun calculateWords(text: String): Int {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return 0
    return trimmed.split("\\s+".toRegex()).size
  }

  private fun triggerAutoSave() {
    autoSaveJob?.cancel()
    autoSaveJob = viewModelScope.launch {
      delay(600) // Debounce 600ms
      performSave()
    }
  }

  fun saveImmediately() {
    autoSaveJob?.cancel()
    viewModelScope.launch {
      performSave()
    }
  }

  private suspend fun performSave() {
    val state = _uiState.value
    // Don't create an empty note with no title and no content
    if (state.title.isBlank() && state.content.isBlank()) {
      return
    }

    _uiState.update { it.copy(isSaving = true) }

    val noteToSave = Note(
      id = currentNoteId,
      title = state.title.trim(),
      content = state.content,
      createdAt = if (state.createdAt > 0) state.createdAt else System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis(),
      isPinned = state.isPinned,
      isFavorite = state.isFavorite,
      colorTag = state.colorTag
    )

    val savedId = repository.insertOrUpdate(noteToSave)
    if (currentNoteId <= 0L) {
      currentNoteId = savedId
    }

    _uiState.update {
      it.copy(
        noteId = currentNoteId,
        updatedAt = noteToSave.updatedAt,
        isSaving = false,
        isSaved = true
      )
    }
  }

  fun deleteCurrentNote(onDeleted: () -> Unit) {
    viewModelScope.launch {
      if (currentNoteId > 0L) {
        repository.deleteById(currentNoteId)
      }
      onDeleted()
    }
  }

  companion object {
    fun provideFactory(context: Context, noteId: Long): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val database = NoteDatabase.getDatabase(context.applicationContext)
          val repository = NoteRepositoryImpl(database.noteDao())
          return EditorViewModel(repository, noteId) as T
        }
      }
  }
}
