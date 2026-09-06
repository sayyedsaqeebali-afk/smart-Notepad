package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Note
import com.example.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteCard(
  note: Note,
  onClick: () -> Unit,
  onTogglePin: () -> Unit,
  onToggleFavorite: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cardBorderColor by animateColorAsState(
    targetValue = if (note.isPinned) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    else MaterialTheme.colorScheme.outlineVariant,
    label = "cardBorderColor"
  )

  val cardBgColor = when {
    note.colorTag != 0L -> Color(note.colorTag.toULong()).copy(alpha = 0.12f)
    note.isPinned -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    else -> MaterialTheme.colorScheme.surface
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("note_card_${note.id}")
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBgColor),
    elevation = CardDefaults.cardElevation(
      defaultElevation = if (note.isPinned) 3.dp else 1.dp,
      pressedElevation = 4.dp
    ),
    border = BorderStroke(
      width = if (note.isPinned) 1.5.dp else 1.dp,
      color = cardBorderColor
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top Row: Title + Pin/Favorite indicators
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (note.isPinned) {
            Icon(
              imageVector = Icons.Filled.PushPin,
              contentDescription = stringResource(R.string.unpin_note),
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .size(18.dp)
                .testTag("pinned_indicator_${note.id}")
            )
            Spacer(modifier = Modifier.width(6.dp))
          }

          Text(
            text = if (note.title.isNotBlank()) note.title else "Untitled Note",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = if (note.isPinned) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Quick action icons
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onTogglePin,
            modifier = Modifier
              .size(32.dp)
              .testTag("pin_button_${note.id}")
          ) {
            Icon(
              imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
              contentDescription = if (note.isPinned) stringResource(R.string.unpin_note) else stringResource(R.string.pin_note),
              tint = if (note.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
              .size(32.dp)
              .testTag("favorite_button_${note.id}")
          ) {
            Icon(
              imageVector = if (note.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
              contentDescription = if (note.isFavorite) stringResource(R.string.unfavorite_note) else stringResource(R.string.favorite_note),
              tint = if (note.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Content Preview
      if (note.content.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = note.content,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Row: Date & Counters & Delete button
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Date & Stats
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = DateUtils.formatTimestamp(note.updatedAt),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )

          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
          )

          Text(
            text = "${note.wordCount} words",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )

          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
          )

          Text(
            text = "${note.characterCount} chars",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
          )
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(32.dp)
            .testTag("delete_note_${note.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
