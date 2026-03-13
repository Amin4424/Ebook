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
            item { WalletHeader(coinBalance = uiState.coinBalance) }
            item { ActionButtons() }
            item { CoinPackagesSection(packages = uiState.packages) }
            item { ReadingStreakSection(streak = uiState.readingStreak, goal = uiState.streakGoal, weekDays = uiState.weekDays) }
            item { TransactionHistorySection(transactions = uiState.transactions) }
        }
    }
}

@Composable
private fun WalletHeader(coinBalance: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colors = listOf(Navy800, DarkBackground)))
            .padding(24.dp)
    ) {
        IconButton(onClick = { }, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "تنظیمات", tint = TextOnDarkSecondary)
        }

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Text(
                text = "کیف پول",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextOnDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "موجودی سکه", style = MaterialTheme.typography.bodyMedium, color = TextOnDarkSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Filled.MonetizationOn, contentDescription = null, tint = Gold400, modifier = Modifier.size(32.dp))
                Text(
                    text = "$coinBalance",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 42.sp),
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
        Button(
            onClick = { },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CardWhite, contentColor = Navy900)
        ) {
            Text(text = "خرید سکه", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
        OutlinedButton(
            onClick = { },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextOnDark),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true)
        ) {
            Text(text = "تاریخچه", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun CoinPackagesSection(packages: List<CoinPackage>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(8.dp), color = DarkCard) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Filled.CardGiftcard, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
                    Text(text = "کد تخفیف", style = MaterialTheme.typography.labelMedium, color = Gold300)
                }
            }
            Text(
                text = "بسته‌های سکه",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = TextOnDark
            )
        }
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
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(shape = RoundedCornerShape(20.dp), color = if (coinPackage.isHighlighted) Navy900 else DarkSurface) {
                Text(
                    text = coinPackage.price,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (coinPackage.isHighlighted) Gold400 else TextOnDark,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${coinPackage.coins} سکه",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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
                    Text(text = coinPackage.description, style = MaterialTheme.typography.bodySmall, color = contentColor.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun ReadingStreakSection(streak: Int, goal: Int, weekDays: List<Boolean>) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
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
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "هدف: $goal روز", style = MaterialTheme.typography.bodySmall, color = TextOnDarkSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "$streak روز پیاپی!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextOnDark
                        )
                        Icon(imageVector = Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Gold400, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    weekDays.forEachIndexed { _, completed ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (completed) Gold400 else DarkSurface)
                                .then(if (!completed) Modifier.border(1.dp, TextOnDarkSecondary.copy(alpha = 0.3f), CircleShape) else Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            if (completed) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Navy900, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionHistorySection(transactions: List<Transaction>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Text(
            text = "تاریخچه تراکنش‌ها",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = TextOnDark,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        transactions.forEach { tx ->
            TransactionItem(transaction = tx)
            HorizontalDivider(color = TextOnDarkSecondary.copy(alpha = 0.1f), thickness = 0.5.dp)
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = transaction.amount,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (transaction.isDeposit) SuccessGreen else ErrorRed
            )
            Text(text = transaction.date, style = MaterialTheme.typography.labelSmall, color = TextOnDarkSecondary)
        }
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextOnDark,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (transaction.isDeposit) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (transaction.isDeposit) Icons.Filled.AddCircle else Icons.Filled.RemoveCircle,
                contentDescription = null,
                tint = if (transaction.isDeposit) SuccessGreen else ErrorRed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
