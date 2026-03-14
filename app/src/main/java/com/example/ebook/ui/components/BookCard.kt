package com.example.ebook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ebook.data.model.Book
import com.example.ebook.ui.theme.*
import com.example.ebook.ui.components.bounceClick
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var downloadState by remember { mutableStateOf(DownloadState.IDLE) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .width(140.dp)
            .bounceClick(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Rating badge
            if (book.rating > 0) {
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Gold400,
                    contentColor = Navy900
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(12.dp))
                        Text(text = String.format("%.1f", book.rating), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Download button overlay (bottom-end)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .clickable {
                        if (downloadState == DownloadState.IDLE) {
                            downloadState = DownloadState.DOWNLOADING
                            scope.launch {
                                for (i in 1..10) {
                                    delay(200)
                                    downloadProgress = i / 10f
                                }
                                downloadState = DownloadState.DONE
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when (downloadState) {
                    DownloadState.IDLE -> Icon(
                        Icons.Filled.Download, contentDescription = "دانلود",
                        tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp)
                    )
                    DownloadState.DOWNLOADING -> CircularProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier.size(20.dp),
                        color = Gold400,
                        strokeWidth = 2.dp
                    )
                    DownloadState.DONE -> Icon(
                        Icons.Filled.Check, contentDescription = "دانلود شد",
                        tint = SuccessGreen, modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = book.title,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private enum class DownloadState { IDLE, DOWNLOADING, DONE }
