package com.example.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.editor.EditorViewModel
import com.example.ui.notes.NotesViewModel
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.SmartNotepadTheme

@Composable
fun SmartNotepadApp(
  notesViewModel: NotesViewModel
) {
  val navController = rememberNavController()
  val themeMode by notesViewModel.themeMode.collectAsStateWithLifecycle()

  SmartNotepadTheme(themeMode = themeMode) {
    NavHost(
      navController = navController,
      startDestination = "home"
    ) {
      composable("home") {
        val uiState by notesViewModel.uiState.collectAsStateWithLifecycle()
        HomeScreen(
          uiState = uiState,
          onSearchQueryChanged = notesViewModel::onSearchQueryChanged,
          onFilterSelected = notesViewModel::onFilterSelected,
          onSortOptionSelected = notesViewModel::onSortOptionSelected,
          onTogglePin = notesViewModel::togglePin,
          onToggleFavorite = notesViewModel::toggleFavorite,
          onDeleteNote = notesViewModel::deleteNote,
          onRestoreDeletedNote = notesViewModel::restoreLastDeletedNote,
          onNoteClick = { noteId -> navController.navigate("editor/$noteId") },
          onNewNoteClick = { navController.navigate("editor/0") },
          onSettingsClick = { navController.navigate("settings") }
        )
      }

      composable(
        route = "editor/{noteId}",
        arguments = listOf(
          navArgument("noteId") {
            type = NavType.LongType
            defaultValue = 0L
          }
        )
      ) { backStackEntry ->
        val context = LocalContext.current
        val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
        val editorViewModel: EditorViewModel = viewModel(
          factory = EditorViewModel.provideFactory(context, noteId),
          key = "editor_$noteId"
        )
        val editorUiState by editorViewModel.uiState.collectAsStateWithLifecycle()

        EditorScreen(
          uiState = editorUiState,
          onTitleChanged = editorViewModel::onTitleChanged,
          onContentChanged = editorViewModel::onContentChanged,
          onTogglePin = editorViewModel::togglePin,
          onToggleFavorite = editorViewModel::toggleFavorite,
          onColorTagSelected = editorViewModel::setColorTag,
          onSaveImmediately = editorViewModel::saveImmediately,
          onDeleteNote = {
            editorViewModel.deleteCurrentNote {
              navController.popBackStack()
            }
          },
          onBackClick = {
            navController.popBackStack()
          }
        )
      }

      composable("settings") {
        SettingsScreen(
          currentThemeMode = themeMode,
          onThemeModeSelected = notesViewModel::setThemeMode,
          onBackClick = { navController.popBackStack() }
        )
      }
    }
  }
}
