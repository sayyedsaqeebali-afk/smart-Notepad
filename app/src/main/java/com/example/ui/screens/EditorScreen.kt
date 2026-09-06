package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.editor.EditorUiState
import com.example.ui.theme.NoteColorAmber
import com.example.ui.theme.NoteColorBlue
import com.example.ui.theme.NoteColorGreen
import com.example.ui.theme.NoteColorPurple
import com.example.ui.theme.NoteColorRose
import com.example.util.DateUtils

private val AvailableColorTags = listOf(
  0L to "Default",
  0xFF3B82F6 to "Blue",
  0xFF10B981 to "Green",
  0xFF8B5CF6 to "Purple",
  0xFFF59E0B to "Amber",
  0xFFF43F5E to "Rose"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
  uiState: EditorUiState,
  onTitleChanged: (String) -> Unit,
  onContentChanged: (String) -> Unit,
  onTogglePin: () -> Unit,
  onToggleFavorite: () -> Unit,
  onColorTagSelected: (Long) -> Unit,
  onSaveImmediately: () -> Unit,
  onDeleteNote: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showDeleteDialog by remember { mutableStateOf(false) }

  // Intercept system back press to ensure auto-save is persisted
  BackHandler {
    onSaveImmediately()
    onBackClick()
  }

  val editorBgColor = when {
    uiState.colorTag != 0L -> Color(uiState.colorTag.toULong()).copy(alpha = 0.05f)
    else -> MaterialTheme.colorScheme.background
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .imePadding(),
    containerColor = editorBgColor,
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (uiState.isSaving) {
              CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = stringResource(R.string.saving),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            } else if (uiState.isSaved) {
              Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = stringResource(R.string.saved),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            } else {
              Text(
                text = "Editing…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        navigationIcon = {
          IconButton(
            onClick = {
              onSaveImmediately()
              onBackClick()
            },
            modifier = Modifier.testTag("editor_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back)
            )
          }
        },
        actions = {
          // Pin Action
          IconButton(
            onClick = onTogglePin,
            modifier = Modifier.testTag("editor_pin_button")
          ) {
            Icon(
              imageVector = if (uiState.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
              contentDescription = if (uiState.isPinned) stringResource(R.string.unpin_note) else stringResource(R.string.pin_note),
              tint = if (uiState.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Favorite Action
          IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.testTag("editor_favorite_button")
          ) {
            Icon(
              imageVector = if (uiState.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
              contentDescription = if (uiState.isFavorite) stringResource(R.string.unfavorite_note) else stringResource(R.string.favorite_note),
              tint = if (uiState.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Delete Action (only for existing saved notes)
          if (uiState.noteId > 0L) {
            IconButton(
              onClick = { showDeleteDialog = true },
              modifier = Modifier.testTag("editor_delete_button")
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = MaterialTheme.colorScheme.error
              )
            }
          }

          // Manual Save Action
          IconButton(
            onClick = onSaveImmediately,
            modifier = Modifier.testTag("editor_save_button")
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = stringResource(R.string.save),
              tint = MaterialTheme.colorScheme.primary
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Info Bar (Last modified time + Word counter + Character counter)
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Last edited timestamp
          Text(
            text = if (uiState.updatedAt > 0) {
              stringResource(R.string.last_modified, DateUtils.formatTimestamp(uiState.updatedAt))
            } else "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Live Counters
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
              Text(
                text = "${uiState.wordCount} ${stringResource(R.string.words)}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                  .padding(horizontal = 8.dp, vertical = 4.dp)
                  .testTag("word_counter")
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
              Text(
                text = "${uiState.charCount} ${stringResource(R.string.characters)}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                  .padding(horizontal = 8.dp, vertical = 4.dp)
                  .testTag("char_counter")
              )
            }
          }
        }
      }

      // Color Tag Selector Row
      Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          items(AvailableColorTags) { (tagValue, name) ->
            val isSelected = uiState.colorTag == tagValue
            val color = if (tagValue == 0L) MaterialTheme.colorScheme.surfaceVariant else Color(tagValue.toULong())

            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                  if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                  else Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                )
                .clickable { onColorTagSelected(tagValue) }
                .testTag("color_tag_$name"),
              contentAlignment = Alignment.Center
            ) {
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = if (tagValue == 0L) MaterialTheme.colorScheme.onSurface else Color.White,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }

      // Title Input
      TextField(
        value = uiState.title,
        onValueChange = onTitleChanged,
        placeholder = {
          Text(
            text = stringResource(R.string.title_hint),
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
          )
        },
        textStyle = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        ),
        singleLine = false,
        maxLines = 3,
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          disabledContainerColor = Color.Transparent,
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .testTag("editor_title_input")
      )

      // Content Input
      TextField(
        value = uiState.content,
        onValueChange = onContentChanged,
        placeholder = {
          Text(
            text = stringResource(R.string.content_hint),
            style = MaterialTheme.typography.bodyLarge.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              lineHeight = 24.sp
            )
          )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
          color = MaterialTheme.colorScheme.onSurface,
          lineHeight = 24.sp
        ),
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          disabledContainerColor = Color.Transparent,
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 8.dp)
          .testTag("editor_content_input")
      )
    }
  }

  // Delete Confirmation Dialog
  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text(stringResource(R.string.confirm_delete_title)) },
      text = { Text(stringResource(R.string.confirm_delete_msg)) },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            onDeleteNote()
          },
          modifier = Modifier.testTag("dialog_confirm_delete_editor")
        ) {
          Text(
            text = stringResource(R.string.delete),
            color = MaterialTheme.colorScheme.error
          )
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showDeleteDialog = false },
          modifier = Modifier.testTag("dialog_cancel_delete_editor")
        ) {
          Text(stringResource(R.string.cancel))
        }
      }
    )
  }
}
