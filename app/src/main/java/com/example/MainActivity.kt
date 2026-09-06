package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.SmartNotepadApp
import com.example.ui.notes.NotesViewModel

class MainActivity : ComponentActivity() {

  private val notesViewModel: NotesViewModel by viewModels {
    NotesViewModel.provideFactory(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SmartNotepadApp(notesViewModel = notesViewModel)
    }
  }
}

/**
 * Compatibility composable for screenshot testing.
 */
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}
