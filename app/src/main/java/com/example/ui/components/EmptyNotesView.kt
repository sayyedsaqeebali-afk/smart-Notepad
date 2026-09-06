package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.notes.NoteFilter

@Composable
fun EmptyNotesView(
  isSearching: Boolean,
  selectedFilter: NoteFilter,
  onActionClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val icon: ImageVector
  val title: String
  val description: String
  val actionButtonText: String

  when {
    isSearching -> {
      icon = Icons.Filled.SearchOff
      title = stringResource(R.string.empty_search_title)
      description = stringResource(R.string.empty_search_desc)
      actionButtonText = stringResource(R.string.clear_search)
    }
    selectedFilter == NoteFilter.PINNED -> {
      icon = Icons.Filled.Description
      title = stringResource(R.string.empty_filter_title, "pinned")
      description = stringResource(R.string.empty_filter_desc, "pinned")
      actionButtonText = stringResource(R.string.add_note)
    }
    selectedFilter == NoteFilter.FAVORITES -> {
      icon = Icons.Filled.StarBorder
      title = stringResource(R.string.empty_filter_title, "favorite")
      description = stringResource(R.string.empty_filter_desc, "favorite")
      actionButtonText = stringResource(R.string.add_note)
    }
    else -> {
      icon = Icons.Filled.Description
      title = stringResource(R.string.empty_notes_title)
      description = stringResource(R.string.empty_notes_desc)
      actionButtonText = stringResource(R.string.add_note)
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp)
      .testTag("empty_notes_view"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Decorative circular background for the icon
    Box(
      modifier = Modifier
        .size(96.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(48.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (isSearching) {
      OutlinedButton(
        onClick = onActionClick,
        modifier = Modifier.testTag("empty_state_action_button")
      ) {
        Icon(
          imageVector = Icons.Filled.Close,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(actionButtonText)
      }
    } else {
      Button(
        onClick = onActionClick,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.testTag("empty_state_action_button")
      ) {
        Icon(
          imageVector = Icons.Filled.Add,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(actionButtonText)
      }
    }
  }
}
