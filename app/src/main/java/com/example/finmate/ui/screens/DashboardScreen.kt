package com.example.finmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finmate.data.model.Transaction
import com.example.finmate.ui.theme.IncomeGreen
import com.example.finmate.ui.theme.ExpenseRed
import com.example.finmate.ui.theme.PrimaryBlue
import com.example.finmate.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val stats by viewModel.monthlyStats.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val recentTransactions = transactions.take(5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        BalanceCard(stats)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (recentTransactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No recent transactions", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(recentTransactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun BalanceCard(stats: FinanceViewModel.MonthlyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.8f))
                    )
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Net Balance",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", stats.netBalance)}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    BalanceIndicator(
                        label = "Income",
                        amount = stats.totalIncome,
                        icon = Icons.Default.ArrowUpward,
                        color = IncomeGreen
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    BalanceIndicator(
                        label = "Expense",
                        amount = stats.totalExpense,
                        icon = Icons.Default.ArrowDownward,
                        color = ExpenseRed
                    )
                }
            }
        }
    }
}

@Composable
fun BalanceIndicator(label: String, amount: Double, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(6.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
            Text(text = "$${String.format(Locale.US, "%.0f", amount)}", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    ListItem(
        headlineContent = { Text(transaction.note.ifEmpty { transaction.category }, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(transaction.category) },
        trailingContent = {
            val prefix = if (transaction.type == Transaction.TYPE_INCOME) "+" else "-"
            val color = if (transaction.type == Transaction.TYPE_INCOME) IncomeGreen else ExpenseRed
            Text(
                text = "$prefix $${String.format(Locale.US, "%.2f", transaction.amount)}",
                color = color,
                fontWeight = FontWeight.Bold
            )
        },
        leadingContent = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
