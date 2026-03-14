package com.example.ebook.ui.screens.bookdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ebook.data.model.Review
import com.example.ebook.ui.theme.*

@Composable
fun BookDetailsScreen(
    bookId: Int,
    onBackClick: () -> Unit,
    onReadClick: () -> Unit,
    viewModel: BookDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Gold400
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Full-bleed cover header
                    item(key = "header") {
                        BookCoverHeader(
                            coverUrl = uiState.book?.coverUrl ?: "",
                            onBackClick = onBackClick
                        )
                    }

                    // Info card overlapping header
                    item(key = "info") {
                        BookInfoCard(uiState = uiState)
                    }

                    // Chapter list header
                    item(key = "toc_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${uiState.chapters.size} فصل",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "فهرست فصل‌ها",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Chapter items
                    itemsIndexed(uiState.chapters, key = { _, ch -> ch.index }) { _, chapter ->
                        ChapterRow(
                            chapter = chapter,
                            walletBalance = uiState.walletBalance,
                            onPurchase = { viewModel.purchaseChapter(chapter.index) }
                        )
                    }

                    // Reviews section
                    item(key = "reviews_header") {
                        ReviewsHeader(
                            reviewCount = uiState.reviews.size,
                            onAddReview = { viewModel.toggleReviewDialog(true) }
                        )
                    }

                    itemsIndexed(uiState.reviews, key = { _, r -> r.id }) { _, review ->
                        ReviewCard(review = review)
                    }

                    // Sync section
                    item(key = "sync") {
                        SyncSection(
                            isSyncing = uiState.isSyncing,
                            syncSuccess = uiState.syncSuccess,
                            onSync = { viewModel.simulateSync() }
                        )
                    }
                }

                // Sticky bottom button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Button(
                        onClick = onReadClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "شروع مطالعه",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Review dialog
            if (uiState.showReviewDialog) {
                WriteReviewDialog(
                    reviewText = uiState.userReviewText,
                    stars = uiState.userReviewStars,
                    onTextChange = viewModel::setUserReviewText,
                    onStarsChange = viewModel::setUserReviewStars,
                    onSubmit = { viewModel.submitReview() },
                    onDismiss = { viewModel.toggleReviewDialog(false) }
                )
            }
        }
    }
}

@Composable
private fun BookCoverHeader(coverUrl: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = coverUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Gradient fade to background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 48.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "اشتراک‌گذاری", tint = Color.White)
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(imageVector = Icons.Filled.Bookmark, contentDescription = "نشانک", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun BookInfoCard(uiState: BookDetailsUiState) {
    val book = uiState.book ?: return
    var synopsisExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-24).dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Title + Author
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Text(
                text = "اثر ${book.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < book.rating.toInt()) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%.1f", book.rating),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BookStat(label = "خوانندگان", value = "۱۲۶+")
                VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                BookStat(label = "زبان", value = "فارسی")
                VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                BookStat(label = "تعداد صفحات", value = "${(book.pages.size * 40 + 86)}")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(16.dp))

            // Synopsis
            Text(
                text = "خلاصه داستان",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Gold500,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.summary.ifBlank { book.pages.firstOrNull()?.take(200) ?: "" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (synopsisExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = if (synopsisExpanded) "بستن" else "ادامه مطلب",
                style = MaterialTheme.typography.labelLarge,
                color = Gold500,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clickable { synopsisExpanded = !synopsisExpanded },
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun BookStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ChapterRow(
    chapter: ChapterInfo,
    walletBalance: Int,
    onPurchase: () -> Unit
) {
    val canAfford = walletBalance >= chapter.coinPrice

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Badge: Free (cyan) or Coin price (gold)
        if (chapter.isFree) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF00BCD4).copy(alpha = 0.15f)
            ) {
                Text(
                    text = "رایگان",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF00BCD4),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        } else {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (canAfford) Gold400.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                modifier = Modifier.clickable { onPurchase() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${chapter.coinPrice}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (canAfford) Gold500 else MaterialTheme.colorScheme.error
                    )
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = null,
                        tint = if (canAfford) Gold400 else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Chapter info
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(
                text = chapter.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            )
            Text(
                text = "${chapter.pageCount} صفحه",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Chapter number
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${chapter.index + 1}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
    )
}

@Composable
private fun ReviewsHeader(reviewCount: Int, onAddReview: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onAddReview,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold500)
        ) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "نظر بده", style = MaterialTheme.typography.labelLarge)
        }
        Text(
            text = "نظرات ($reviewCount)",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < review.stars) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = review.authorName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SyncSection(
    isSyncing: Boolean,
    syncSuccess: Boolean?,
    onSync: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "پشتیبان‌گیری",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = syncSuccess == true) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    Text(text = "همگام‌سازی موفق", style = MaterialTheme.typography.labelMedium, color = SuccessGreen)
                }
            }
            Button(
                onClick = onSync,
                enabled = !isSyncing,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(imageVector = Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSyncing) "در حال همگام‌سازی..." else "همگام‌سازی",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun WriteReviewDialog(
    reviewText: String,
    stars: Int,
    onTextChange: (String) -> Unit,
    onStarsChange: (Int) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "نظر خود را بنویسید",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Star selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < stars) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { onStarsChange(i + 1) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("نظر خود را اینجا بنویسید...", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold500)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                    TextButton(onClick = onDismiss) { Text("انصراف") }
                    Button(
                        onClick = onSubmit,
                        colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Navy900),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("ثبت نظر", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
