package com.example.ebook.ui.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ebook.ui.components.BottomNavBar
import com.example.ebook.ui.theme.*

@Composable
fun WalletScreen(
    navController: NavController,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Wallet Header
            item {
                WalletHeader(coinBalance = uiState.coinBalance)
            }

            // Action Buttons
            item {
                ActionButtons()
            }

            // Coin Packages Section
            item {
                CoinPackagesSection(
                    packages = uiState.packages
                )
            }

            // Reading Streak Section
            item {
                ReadingStreakSection(
                    streak = uiState.readingStreak,
                    goal = uiState.streakGoal,
                    weekDays = uiState.weekDays
                )
            }
        }
    }
}

@Composable
private fun WalletHeader(coinBalance: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Navy800,
                        DarkBackground
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Settings icon
        IconButton(
            onClick = { },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "تنظیمات",
                tint = TextOnDarkSecondary
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "کیف پول",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextOnDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "موجودی سکه",
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnDarkSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.MonetizationOn,
                    contentDescription = null,
                    tint = Gold400,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "۴۵۰",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 42.sp
                    ),
                    color = Gold400
                )
            }
        }
    }
}

@Composable
private fun ActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Purchase button
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardWhite,
                contentColor = Navy900
            )
        ) {
            Text(
                text = "خرید سکه",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        // History button
        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextOnDark
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true)
        ) {
            Text(
                text = "تاریخچه",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun CoinPackagesSection(packages: List<CoinPackage>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Section header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Discount code
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkCard
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CardGiftcard,
                        contentDescription = null,
                        tint = Gold400,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "کد تخفیف",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gold300
                    )
                }
            }

            Text(
                text = "بسته‌های سکه",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextOnDark
            )
        }

        // Packages
        packages.forEach { pkg ->
            CoinPackageCard(pkg)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CoinPackageCard(coinPackage: CoinPackage) {
    val cardColor = if (coinPackage.isHighlighted) Gold400 else DarkCard
    val contentColor = if (coinPackage.isHighlighted) Navy900 else TextOnDark

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Price badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (coinPackage.isHighlighted) Navy900 else DarkSurface
            ) {
                Text(
                    text = coinPackage.price,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (coinPackage.isHighlighted) Gold400 else TextOnDark,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${coinPackage.coins} سکه",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor
                    )
                    Icon(
                        imageVector = Icons.Filled.MonetizationOn,
                        contentDescription = null,
                        tint = if (coinPackage.isHighlighted) Navy900 else Gold400,
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (coinPackage.description.isNotEmpty()) {
                    Text(
                        text = coinPackage.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadingStreakSection(
    streak: Int,
    goal: Int,
    weekDays: List<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "زنجیره مطالعه",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = TextOnDark,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "هدف: $goal روز",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextOnDarkSecondary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$streak روز پیاپی!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextOnDark
                        )
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Week days visualization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    weekDays.forEachIndexed { index, completed ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (completed) Gold400
                                    else DarkSurface
                                )
                                .then(
                                    if (!completed) Modifier.border(
                                        1.dp,
                                        TextOnDarkSecondary.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (completed) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Navy900,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
