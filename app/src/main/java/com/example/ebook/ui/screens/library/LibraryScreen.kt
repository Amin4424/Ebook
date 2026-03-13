package com.example.ebook.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ebook.data.model.Book
import com.example.ebook.ui.components.BottomNavBar
import com.example.ebook.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    onBookClick: (Int) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "کتاب‌های من",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab row
            val tabs = listOf(
                LibraryTab.READING to "در حال خواندن",
                LibraryTab.FINISHED to "خوانده شده",
                LibraryTab.WANT_TO_READ to "می‌خواهم بخوانم"
            )
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == uiState.activeTab },
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = Gold500,
                edgePadding = 16.dp
            ) {
                tabs.forEach { (tab, label) ->
                    Tab(
                        selected = uiState.activeTab == tab,
                        onClick = { viewModel.setTab(tab) },
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (uiState.activeTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Gold500,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            val books = when (uiState.activeTab) {
                LibraryTab.READING -> uiState.readingBooks
                LibraryTab.FINISHED -> uiState.finishedBooks
                LibraryTab.WANT_TO_READ -> uiState.wantToReadBooks
            }
            val tabIcon = when (uiState.activeTab) {
                LibraryTab.READING -> Icons.Filled.AutoStories
                LibraryTab.FINISHED -> Icons.Filled.CheckCircle
                LibraryTab.WANT_TO_READ -> Icons.Filled.Bookmarks
            }

            if (books.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(imageVector = tabIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(80.dp))
                        Text(
                            text = "کتابی در این بخش وجود ندارد",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books) { book ->
                        LibraryBookCard(
                            book = book,
                            tab = uiState.activeTab,
                            onClick = { onBookClick(book.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryBookCard(
    book: Book,
    tab: LibraryTab,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book info
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Status badge
                val (badgeColor, badgeLabel, badgeIcon) = when (tab) {
                    LibraryTab.READING -> Triple(Gold400, "در حال خواندن", Icons.Filled.AutoStories)
                    LibraryTab.FINISHED -> Triple(SuccessGreen, "خوانده شده", Icons.Filled.CheckCircle)
                    LibraryTab.WANT_TO_READ -> Triple(Navy600, "می‌خواهم بخوانم", Icons.Filled.Bookmarks)
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = badgeLabel, style = MaterialTheme.typography.labelSmall, color = badgeColor)
                        Icon(imageVector = badgeIcon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier.size(70.dp, 100.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// Extension to support Triple destructuring for color/label/icon
private operator fun <A, B, C> Triple<A, B, C>.component1() = first
private operator fun <A, B, C> Triple<A, B, C>.component2() = second
private operator fun <A, B, C> Triple<A, B, C>.component3() = third
