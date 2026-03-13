package com.example.ebook.ui.screens.reader

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ebook.ui.theme.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    bookId: Int,
    onBackClick: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val backgroundColor = if (uiState.isNightMode) DarkBackground else OffWhite
    val textColor = if (uiState.isNightMode) TextOnDark else TextPrimary
    val surfaceColor = if (uiState.isNightMode) DarkSurface else CardWhite
    val secondaryTextColor = if (uiState.isNightMode) TextOnDarkSecondary else TextSecondary

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                AnimatedVisibility(
                    visible = uiState.showControls,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = surfaceColor,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "بازگشت",
                                    tint = textColor
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.book?.title ?: "",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = secondaryTextColor
                                )
                                Text(
                                    text = "فصل اول: آشنایی",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "بیشتر",
                                    tint = textColor
                                )
                            }
                        }
                    }
                }

                // Main Content - Reading Area
                var dragOffset by remember { mutableFloatStateOf(0f) }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.toggleControls()
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (abs(dragOffset) > 100) {
                                        if (dragOffset > 0) {
                                            // Swiped right in RTL = next page
                                            viewModel.nextPage()
                                        } else {
                                            // Swiped left in RTL = previous page
                                            viewModel.previousPage()
                                        }
                                    }
                                    dragOffset = 0f
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    dragOffset += dragAmount
                                }
                            )
                        }
                ) {
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        uiState.book?.let { book ->
                            if (uiState.currentPage < book.pages.size) {
                                Text(
                                    text = book.pages[uiState.currentPage],
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = uiState.fontSize.sp,
                                        lineHeight = (uiState.fontSize * 1.8).sp,
                                    ),
                                    color = textColor,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Bottom Controls
                AnimatedVisibility(
                    visible = uiState.showControls,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = surfaceColor,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            // Page indicator and slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${uiState.currentPage + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = secondaryTextColor
                                )

                                if (uiState.totalPages > 1) {
                                    Slider(
                                        value = uiState.currentPage.toFloat(),
                                        onValueChange = { viewModel.goToPage(it.toInt()) },
                                        valueRange = 0f..(uiState.totalPages - 1).toFloat(),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Gold400,
                                            activeTrackColor = Gold400,
                                            inactiveTrackColor = if (uiState.isNightMode)
                                                Navy600 else LightGray
                                        )
                                    )
                                }

                                Text(
                                    text = "${uiState.totalPages}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = secondaryTextColor
                                )
                            }

                            Divider(
                                color = if (uiState.isNightMode) Navy600 else LightGray,
                                thickness = 0.5.dp
                            )

                            // Control buttons row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Table of Contents
                                ControlButton(
                                    icon = Icons.AutoMirrored.Filled.List,
                                    label = "فهرست",
                                    tint = textColor,
                                    onClick = { }
                                )

                                // Font Size
                                ControlButton(
                                    icon = Icons.Filled.TextFields,
                                    label = "تنظیمات متن",
                                    tint = textColor,
                                    onClick = { },
                                    content = {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = { viewModel.decreaseFontSize() },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Text(
                                                    "A-",
                                                    color = textColor,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Filled.TextFields,
                                                contentDescription = "تنظیمات متن",
                                                modifier = Modifier.size(20.dp),
                                                tint = textColor
                                            )
                                            TextButton(
                                                onClick = { viewModel.increaseFontSize() },
                                                contentPadding = PaddingValues(0.dp),
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Text(
                                                    "A+",
                                                    color = textColor,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                )

                                // Play / Audio (decorative)
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Gold400)
                                        .clickable { },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "پخش صوتی",
                                        tint = Navy900,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                // Night Mode
                                ControlButton(
                                    icon = if (uiState.isNightMode) Icons.Filled.LightMode
                                    else Icons.Filled.DarkMode,
                                    label = "حالت شب",
                                    tint = textColor,
                                    onClick = { viewModel.toggleNightMode() }
                                )

                                // Bookmark
                                ControlButton(
                                    icon = if (uiState.isBookmarked) Icons.Filled.Bookmark
                                    else Icons.Outlined.BookmarkBorder,
                                    label = "نشانه‌ها",
                                    tint = if (uiState.isBookmarked) Gold400 else textColor,
                                    onClick = { viewModel.toggleBookmark() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        if (content != null) {
            content()
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = tint
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
