package com.example.data.repository

import com.example.data.local.NoteDao
import com.example.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
  fun getAllNotes(): Flow<List<Note>>
  fun getNoteById(id: Long): Flow<Note?>
  suspend fun getNoteByIdDirect(id: Long): Note?
  fun searchNotes(query: String): Flow<List<Note>>
  suspend fun insertOrUpdate(note: Note): Long
  suspend fun delete(note: Note)
  suspend fun deleteById(id: Long)
  suspend fun togglePin(id: Long, isPinned: Boolean)
  suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}

class NoteRepositoryImpl(
  private val noteDao: NoteDao
) : NoteRepository {

  override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

  override fun getNoteById(id: Long): Flow<Note?> = noteDao.getNoteById(id)

  override suspend fun getNoteByIdDirect(id: Long): Note? = noteDao.getNoteByIdDirect(id)

  override fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

  override suspend fun insertOrUpdate(note: Note): Long {
    val updatedNote = note.copy(updatedAt = System.currentTimeMillis())
    return noteDao.insertNote(updatedNote)
  }

  override suspend fun delete(note: Note) = noteDao.deleteNote(note)

  override suspend fun deleteById(id: Long) = noteDao.deleteNoteById(id)

  override suspend fun togglePin(id: Long, isPinned: Boolean) {
    noteDao.updatePinStatus(id, isPinned, System.currentTimeMillis())
  }

  override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
    noteDao.updateFavoriteStatus(id, isFavorite, System.currentTimeMillis())
  }
}
