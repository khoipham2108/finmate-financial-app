package com.example.finmate.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finmate.data.model.Transaction
import com.example.finmate.ui.theme.IncomeGreen
import com.example.finmate.ui.theme.IncomeGreenLight
import com.example.finmate.ui.theme.ExpenseRed
import com.example.finmate.ui.theme.ExpenseRedLight
import com.example.finmate.util.formatVND

@Composable
fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == Transaction.TYPE_INCOME
    val categoryIcon = getCategoryIcon(transaction.category)
    val bgColor = if (isIncome) IncomeGreenLight else ExpenseRedLight
    val iconTint = if (isIncome) IncomeGreen else ExpenseRed

    ListItem(
        headlineContent = { Text(transaction.note.ifEmpty { transaction.category }, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(transaction.category, style = MaterialTheme.typography.bodySmall) },
        trailingContent = {
            val prefix = if (isIncome) "+" else "-"
            val color = if (isIncome) IncomeGreen else ExpenseRed
            Text(
                text = "$prefix ${transaction.amount.formatVND()}",
                color = color,
                fontWeight = FontWeight.Bold
            )
        },
        leadingContent = {
            Surface(
                shape = CircleShape,
                color = bgColor,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(12.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food", "dining", "restaurant" -> Icons.Default.Restaurant
        "salary", "income", "bonus" -> Icons.Default.MonetizationOn
        "shopping", "store" -> Icons.Default.ShoppingBag
        "transport", "travel", "taxi", "gas" -> Icons.Default.DirectionsCar
        "entertainment", "movie", "game" -> Icons.Default.SportsEsports
        "health", "medical", "pharmacy" -> Icons.Default.MedicalServices
        "education", "school", "book" -> Icons.Default.School
        "bills", "utility", "rent" -> Icons.Default.ReceiptLong
        else -> Icons.Default.Category
    }
}
