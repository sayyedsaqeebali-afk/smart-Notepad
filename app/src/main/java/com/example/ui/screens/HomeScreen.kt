package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Note
import com.example.ui.components.EmptyNotesView
import com.example.ui.components.NoteCard
import com.example.ui.notes.NoteFilter
import com.example.ui.notes.NoteSort
import com.example.ui.notes.NotesUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  uiState: NotesUiState,
  onSearchQueryChanged: (String) -> Unit,
  onFilterSelected: (NoteFilter) -> Unit,
  onSortOptionSelected: (NoteSort) -> Unit,
  onTogglePin: (Note) -> Unit,
  onToggleFavorite: (Note) -> Unit,
  onDeleteNote: (Note) -> Unit,
  onRestoreDeletedNote: () -> Unit,
  onNoteClick: (Long) -> Unit,
  onNewNoteClick: () -> Unit,
  onSettingsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  var noteToDelete by remember { mutableStateOf<Note?>(null) }
  var sortMenuExpanded by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onNewNoteClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = CircleShape,
        modifier = Modifier.testTag("add_note_fab")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = stringResource(R.string.add_note),
          modifier = Modifier.size(24.dp)
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Top Header & Search Bar
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          // Title Bar with Settings action
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = stringResource(R.string.app_name),
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )

            IconButton(
              onClick = onSettingsClick,
              modifier = Modifier.testTag("settings_button")
            ) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.title_settings),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Search Input Field
          TextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = {
              Text(
                text = stringResource(R.string.search_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            trailingIcon = {
              if (uiState.searchQuery.isNotEmpty()) {
                IconButton(
                  onClick = { onSearchQueryChanged("") },
                  modifier = Modifier.testTag("clear_search_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear_search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
              disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_text_field")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Filter Chips & Sort Button Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Filter Chips
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              item {
                FilterChip(
                  selected = uiState.selectedFilter == NoteFilter.ALL,
                  onClick = { onFilterSelected(NoteFilter.ALL) },
                  label = {
                    Text(
                      text = "${stringResource(R.string.filter_all)} (${uiState.totalCount})"
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                  ),
                  modifier = Modifier.testTag("filter_all_chip")
                )
              }

              item {
                FilterChip(
                  selected = uiState.selectedFilter == NoteFilter.PINNED,
                  onClick = { onFilterSelected(NoteFilter.PINNED) },
                  label = {
                    Text(
                      text = "${stringResource(R.string.filter_pinned)} (${uiState.pinnedCount})"
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                  ),
                  modifier = Modifier.testTag("filter_pinned_chip")
                )
              }

              item {
                FilterChip(
                  selected = uiState.selectedFilter == NoteFilter.FAVORITES,
                  onClick = { onFilterSelected(NoteFilter.FAVORITES) },
                  label = {
                    Text(
                      text = "${stringResource(R.string.filter_favorites)} (${uiState.favoriteCount})"
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                  ),
                  modifier = Modifier.testTag("filter_favorites_chip")
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Sort Menu Button
            Box {
              IconButton(
                onClick = { sortMenuExpanded = true },
                modifier = Modifier.testTag("sort_menu_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Sort,
                  contentDescription = stringResource(R.string.sort_by),
                  tint = MaterialTheme.colorScheme.primary
                )
              }

              DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
              ) {
                DropdownMenuItem(
                  text = { Text(stringResource(R.string.sort_modified_desc)) },
                  trailingIcon = if (uiState.sortOption == NoteSort.MODIFIED_DESC) {
                    { Icon(Icons.Default.Check, null) }
                  } else null,
                  onClick = {
                    onSortOptionSelected(NoteSort.MODIFIED_DESC)
                    sortMenuExpanded = false
                  }
                )
                DropdownMenuItem(
                  text = { Text(stringResource(R.string.sort_modified_asc)) },
                  trailingIcon = if (uiState.sortOption == NoteSort.MODIFIED_ASC) {
                    { Icon(Icons.Default.Check, null) }
                  } else null,
                  onClick = {
                    onSortOptionSelected(NoteSort.MODIFIED_ASC)
                    sortMenuExpanded = false
                  }
                )
                DropdownMenuItem(
                  text = { Text(stringResource(R.string.sort_created_desc)) },
                  trailingIcon = if (uiState.sortOption == NoteSort.CREATED_DESC) {
                    { Icon(Icons.Default.Check, null) }
                  } else null,
                  onClick = {
                    onSortOptionSelected(NoteSort.CREATED_DESC)
                    sortMenuExpanded = false
                  }
                )
                DropdownMenuItem(
                  text = { Text(stringResource(R.string.sort_title_asc)) },
                  trailingIcon = if (uiState.sortOption == NoteSort.TITLE_ASC) {
                    { Icon(Icons.Default.Check, null) }
                  } else null,
                  onClick = {
                    onSortOptionSelected(NoteSort.TITLE_ASC)
                    sortMenuExpanded = false
                  }
                )
                DropdownMenuItem(
                  text = { Text(stringResource(R.string.sort_title_desc)) },
                  trailingIcon = if (uiState.sortOption == NoteSort.TITLE_DESC) {
                    { Icon(Icons.Default.Check, null) }
                  } else null,
                  onClick = {
                    onSortOptionSelected(NoteSort.TITLE_DESC)
                    sortMenuExpanded = false
                  }
                )
              }
            }
          }
        }
      }

      // Content Body (Loading, Empty state, or Notes list)
      when {
        uiState.isLoading -> {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
          }
        }

        uiState.notes.isEmpty() -> {
          EmptyNotesView(
            isSearching = uiState.searchQuery.isNotBlank(),
            selectedFilter = uiState.selectedFilter,
            onActionClick = {
              if (uiState.searchQuery.isNotBlank()) {
                onSearchQueryChanged("")
              } else {
                onNewNoteClick()
              }
            },
            modifier = Modifier.weight(1f)
          )
        }

        else -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .testTag("notes_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(
              items = uiState.notes,
              key = { it.id }
            ) { note ->
              NoteCard(
                note = note,
                onClick = { onNoteClick(note.id) },
                onTogglePin = { onTogglePin(note) },
                onToggleFavorite = { onToggleFavorite(note) },
                onDelete = { noteToDelete = note }
              )
            }
          }
        }
      }
    }
  }

  // Delete Confirmation Dialog
  noteToDelete?.let { note ->
    AlertDialog(
      onDismissRequest = { noteToDelete = null },
      title = { Text(stringResource(R.string.confirm_delete_title)) },
      text = { Text(stringResource(R.string.confirm_delete_msg)) },
      confirmButton = {
        TextButton(
          onClick = {
            onDeleteNote(note)
            noteToDelete = null
            scope.launch {
              val result = snackbarHostState.showSnackbar(
                message = "Note deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
              )
              if (result == SnackbarResult.ActionPerformed) {
                onRestoreDeletedNote()
              }
            }
          },
          modifier = Modifier.testTag("dialog_confirm_delete")
        ) {
          Text(
            text = stringResource(R.string.delete),
            color = MaterialTheme.colorScheme.error
          )
        }
      },
      dismissButton = {
        TextButton(
          onClick = { noteToDelete = null },
          modifier = Modifier.testTag("dialog_cancel_delete")
        ) {
          Text(stringResource(R.string.cancel))
        }
      }
    )
  }
}
