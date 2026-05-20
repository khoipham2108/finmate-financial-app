package com.example.finmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finmate.data.model.Transaction
import com.example.finmate.viewmodel.FinanceViewModel
import java.util.Locale

@Composable
fun ReportScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val expenseTransactions = transactions.filter { it.type == Transaction.TYPE_EXPENSE }
    val totalExpense = expenseTransactions.sumOf { it.amount }
    
    val categoryTotals = expenseTransactions.groupBy { it.category }
        .mapValues { it.value.sumOf { t -> t.amount } }
        .toList()
        .sortedByDescending { it.second }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Expense Report",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (expenseTransactions.isEmpty()) {
            Text("No expenses recorded yet to show reports.", color = Color.Gray)
        } else {
            Text(
                text = "Total Expenses: $${String.format(Locale.US, "%.2f", totalExpense)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(categoryTotals) { (category, amount) ->
                    CategoryProgressItem(category, amount, totalExpense)
                }
            }
        }
    }
}

@Composable
fun CategoryProgressItem(category: String, amount: Double, total: Double) {
    val progress = (amount / total).toFloat()
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = category, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$${String.format(Locale.US, "%.2f", amount)}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(androidx.compose.ui.Alignment.End)
        )
    }
}
