package com.example.ebook.ui.screens.reader

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

    val readerColors = when (uiState.readerTheme) {
        ReaderTheme.DARK -> ReaderColors(DarkBackground, TextOnDark, DarkSurface, TextOnDarkSecondary)
        ReaderTheme.LIGHT -> ReaderColors(OffWhite, TextPrimary, CardWhite, TextSecondary)
        ReaderTheme.SEPIA -> ReaderColors(SepiaBackground, SepiaText, SepiaSurface, SepiaSecondary)
        ReaderTheme.OLED -> ReaderColors(OledBackground, OledText, OledSurface, OledSecondary)
    }
    val backgroundColor = readerColors.background
    val textColor = readerColors.text
    val surfaceColor = readerColors.surface

    // AI Chat Bottom Sheet
    if (uiState.isChatOpen) {
        ChatBottomSheet(
            messages = uiState.chatMessages,
            isLoading = uiState.isChatLoading,
            onDismiss = { viewModel.toggleChat() },
            onSendMessage = { viewModel.sendChatMessage(it) }
        )
    }
    val secondaryTextColor = readerColors.secondaryText

    // Table of Contents chapters based on book pages
    val chapters = uiState.book?.pages?.mapIndexed { i, _ -> "فصل ${i + 1}" } ?: emptyList()

    // Sample reviews
    val reviews = remember {
        listOf(
            MockReview("احمد رضایی", 5, "کتاب فوق‌العاده‌ای بود، به همه پیشنهاد می‌دهم!"),
            MockReview("مریم محمدی", 4, "روایت جذاب و پر از جزئیات دقیق."),
            MockReview("علی حسینی", 5, "یکی از بهترین آثار ادبی که خوانده‌ام.")
        )
    }

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
                    Surface(modifier = Modifier.fillMaxWidth(), color = surfaceColor, shadowElevation = 4.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = textColor)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = uiState.book?.title ?: "", style = MaterialTheme.typography.titleSmall, color = secondaryTextColor)
                                Text(
                                    text = "فصل ${uiState.currentPage + 1}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textColor
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.toggleToc() }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "فهرست", tint = textColor)
                            }
                        }
                    }
                }

                // Reading content area
                var dragOffset by remember { mutableFloatStateOf(0f) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            viewModel.toggleControls()
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (abs(dragOffset) > 100) {
                                        if (dragOffset > 0) viewModel.nextPage() else viewModel.previousPage()
                                    }
                                    dragOffset = 0f
                                },
                                onHorizontalDrag = { _, dragAmount -> dragOffset += dragAmount }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .verticalScroll(rememberScrollState())
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

                            // Book Reviews (shown on last page)
                            if (uiState.currentPage == book.pages.size - 1) {
                                Spacer(modifier = Modifier.height(32.dp))
                                BookReviewsSection(reviews = reviews, textColor = textColor, secondaryTextColor = secondaryTextColor, surfaceColor = surfaceColor)
                            }
                        }
                    }
                }

                // Audio Player Overlay
                AnimatedVisibility(
                    visible = uiState.showAudioPlayer,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    AudioPlayerBar(
                        isPlaying = uiState.isAudioPlaying,
                        progress = uiState.audioProgress,
                        speed = uiState.audioSpeed,
                        surfaceColor = surfaceColor,
                        textColor = textColor,
                        onPlayPause = { viewModel.toggleAudioPlayback() },
                        onProgressChange = { viewModel.setAudioProgress(it) },
                        onSpeedChange = { viewModel.setAudioSpeed(it) }
                    )
                }

                // Bottom Controls
                AnimatedVisibility(
                    visible = uiState.showControls,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Surface(modifier = Modifier.fillMaxWidth(), color = surfaceColor, shadowElevation = 8.dp) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            // Page slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${uiState.currentPage + 1}", style = MaterialTheme.typography.bodySmall, color = secondaryTextColor)
                                if (uiState.totalPages > 1) {
                                    Slider(
                                        value = uiState.currentPage.toFloat(),
                                        onValueChange = { viewModel.goToPage(it.toInt()) },
                                        valueRange = 0f..(uiState.totalPages - 1).toFloat(),
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Gold400,
                                            activeTrackColor = Gold400,
                                            inactiveTrackColor = if (uiState.isNightMode) Navy600 else LightGray
                                        )
                                    )
                                }
                                Text(text = "${uiState.totalPages}", style = MaterialTheme.typography.bodySmall, color = secondaryTextColor)
                            }

                            HorizontalDivider(color = if (uiState.isNightMode) Navy600 else LightGray, thickness = 0.5.dp)

                            // Control buttons — Play centered
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Font size (weight 1)
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        TextButton(onClick = { viewModel.decreaseFontSize() }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(32.dp)) {
                                            Text("A-", color = textColor, fontSize = 12.sp)
                                        }
                                        Icon(imageVector = Icons.Filled.TextFields, contentDescription = null, modifier = Modifier.size(18.dp), tint = textColor)
                                        TextButton(onClick = { viewModel.increaseFontSize() }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(32.dp)) {
                                            Text("A+", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Bookmark (weight 1)
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    ControlButton(
                                        icon = if (uiState.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        label = "نشانه‌ها",
                                        tint = if (uiState.isBookmarked) Gold400 else textColor,
                                        onClick = { viewModel.toggleBookmark() }
                                    )
                                }

                                // Play button — CENTER (weight 0, not weighted so it's naturally centered)
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Gold400)
                                            .clickable { viewModel.toggleAudioPlayer() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.showAudioPlayer) Icons.Filled.Close else Icons.Filled.PlayArrow,
                                            contentDescription = "پخش صوتی",
                                            tint = Navy900,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                // Theme cycle (weight 1)
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    ControlButton(
                                        icon = when (uiState.readerTheme) {
                                            ReaderTheme.DARK -> Icons.Filled.DarkMode
                                            ReaderTheme.LIGHT -> Icons.Filled.LightMode
                                            ReaderTheme.SEPIA -> Icons.Filled.WbSunny
                                            ReaderTheme.OLED -> Icons.Filled.Brightness2
                                        },
                                        label = when (uiState.readerTheme) {
                                            ReaderTheme.DARK -> "تاریک"
                                            ReaderTheme.LIGHT -> "روشن"
                                            ReaderTheme.SEPIA -> "سپیا"
                                            ReaderTheme.OLED -> "OLED"
                                        },
                                        tint = textColor,
                                        onClick = { viewModel.toggleNightMode() }
                                    )
                                }

                                // Highlight toggle (weight 1)
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    ControlButton(
                                        icon = Icons.Filled.Edit,
                                        label = "هایلایت",
                                        tint = textColor,
                                        onClick = { viewModel.setHighlightColor(0xFFFFEB3B) }
                                    )
                                }

                                // Auto-scroll toggle (weight 1)
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    ControlButton(
                                        icon = if (uiState.autoScrollEnabled) Icons.Filled.PauseCircle else Icons.Filled.SlowMotionVideo,
                                        label = "اسکرول",
                                        tint = if (uiState.autoScrollEnabled) Gold400 else textColor,
                                        onClick = { viewModel.setAutoScroll(!uiState.autoScrollEnabled) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Table of Contents Drawer (ModalBottomSheet style)
            if (uiState.showToc) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            viewModel.toggleToc()
                        }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .fillMaxHeight()
                        .align(Alignment.TopEnd)
                        .background(surfaceColor)
                        .padding(top = 48.dp)
                ) {
                    Text(
                        text = "فهرست مطالب",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                    HorizontalDivider(color = textColor.copy(alpha = 0.1f))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(chapters) { index, chapter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.goToPage(index)
                                        viewModel.toggleToc()
                                    }
                                    .background(if (index == uiState.currentPage) Gold400.copy(alpha = 0.15f) else Color.Transparent)
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (index == uiState.currentPage) {
                                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
                                } else {
                                    Spacer(modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = chapter,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (index == uiState.currentPage) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (index == uiState.currentPage) Gold400 else textColor,
                                    textAlign = TextAlign.End
                                )
                            }
                            HorizontalDivider(color = textColor.copy(alpha = 0.05f))
                        }
                    }
                }
            }

            // Auto-scroll FAB indicator
            if (uiState.autoScrollEnabled) {
                Surface(
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 56.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Gold400.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.PlayCircle, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                        Text(text = "پیمایش خودکار", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Navy900)
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioPlayerBar(
    isPlaying: Boolean,
    progress: Float,
    speed: Float,
    surfaceColor: Color,
    textColor: Color,
    onPlayPause: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = surfaceColor, shadowElevation = 12.dp) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "پخش صوتی",
                style = MaterialTheme.typography.labelMedium,
                color = textColor.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = progress,
                onValueChange = onProgressChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(thumbColor = Gold400, activeTrackColor = Gold400, inactiveTrackColor = Gold400.copy(alpha = 0.3f))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { s ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (speed == s) Gold400 else Gold400.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { onSpeedChange(s) }
                        ) {
                            Text(
                                text = "${s}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (speed == s) Navy900 else textColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Play / Pause
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Gold400)
                        .clickable { onPlayPause() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "پخش",
                        tint = Navy900,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BookReviewsSection(
    reviews: List<MockReview>,
    textColor: Color,
    secondaryTextColor: Color,
    surfaceColor: Color
) {
    Column {
        HorizontalDivider(color = textColor.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "نظرات خوانندگان",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(12.dp))
        reviews.forEach { review ->
            ReviewCard(review = review, textColor = textColor, secondaryTextColor = secondaryTextColor, surfaceColor = surfaceColor)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ReviewCard(
    review: MockReview,
    textColor: Color,
    secondaryTextColor: Color,
    surfaceColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = surfaceColor,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.End) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stars
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < review.stars) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(text = review.author, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textColor)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall,
                color = secondaryTextColor,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(22.dp), tint = tint)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = tint, modifier = Modifier.padding(top = 4.dp))
    }
}

data class ReaderColors(
    val background: Color,
    val text: Color,
    val surface: Color,
    val secondaryText: Color
)

data class MockReview(val author: String, val stars: Int, val comment: String)
