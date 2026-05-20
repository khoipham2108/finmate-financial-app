package com.example.finmate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finmate.data.model.Transaction
import com.example.finmate.ui.components.TransactionItem
import com.example.finmate.ui.theme.*
import com.example.finmate.util.formatVND
import com.example.finmate.viewmodel.FinanceViewModel
import java.util.*

@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val stats by viewModel.monthlyStats.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val recentTransactions = transactions.take(10)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            BalanceCard(stats)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Weekly Expenditure",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            WeeklyLineChart(transactions)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recent transactions", color = Color.Gray)
                }
            }
        } else {
            items(recentTransactions) { transaction ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    TransactionItem(transaction)
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}

@Composable
fun BalanceCard(stats: FinanceViewModel.MonthlyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryBlue, Color(0xFF01579B))
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
                    text = stats.netBalance.formatVND(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))
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
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
            Text(text = amount.formatVND(), color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun WeeklyLineChart(transactions: List<Transaction>) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val labelColor = TextSecondary.toArgb()
    
    val weeklyData = remember(transactions) {
        val calendar = Calendar.getInstance()
        val dayExpenses = DoubleArray(7) { 0.0 }
        
        // Current week range
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val startOfWeek = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.timeInMillis

        transactions.filter { 
            it.type == Transaction.TYPE_EXPENSE && 
            it.date.toDate().time in startOfWeek..endOfWeek 
        }.forEach { 
            calendar.time = it.date.toDate()
            val day = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Map to 0 (Mon) - 6 (Sun)
            dayExpenses[day] += it.amount
        }
        dayExpenses
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height - 30.dp.toPx() // Reserve space for labels
                val maxExpense = (weeklyData.maxOrNull() ?: 1.0).coerceAtLeast(1.0).toFloat()
                
                val points = weeklyData.mapIndexed { index, expense ->
                    val x = index * (width / 6f)
                    val y = height - (expense.toFloat() / maxExpense * height * 0.8f)
                    Offset(x, y)
                }

                val path = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            val p0 = points[i-1]
                            val p1 = points[i]
                            // Cubic curve for smoothness
                            cubicTo(
                                (p0.x + p1.x) / 2, p0.y,
                                (p0.x + p1.x) / 2, p1.y,
                                p1.x, p1.y
                            )
                        }
                    }
                }

                // Draw background gradient under path
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryBlue.copy(alpha = 0.3f), Color.Transparent)
                    )
                )

                // Draw the line
                drawPath(
                    path = path,
                    color = PrimaryBlue,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Draw points and labels
                val paint = android.graphics.Paint().apply {
                    color = labelColor
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }

                points.forEachIndexed { index, point ->
                    drawCircle(color = PrimaryBlue, radius = 4.dp.toPx(), center = point)
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = point)
                    
                    // Draw day label
                    drawContext.canvas.nativeCanvas.drawText(
                        daysOfWeek[index],
                        point.x,
                        size.height - 5.dp.toPx(),
                        paint
                    )
                }
            }
        }
    }
}
