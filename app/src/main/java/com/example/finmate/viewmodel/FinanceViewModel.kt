package com.example.finmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finmate.data.model.Transaction
import com.example.finmate.data.repository.FinanceRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FinanceViewModel(
    private val repository: FinanceRepository = FinanceRepository()
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = repository.getTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupedTransactions: StateFlow<Map<String, List<Transaction>>> = transactions
        .map { list ->
            list.groupBy { formatDate(it.date) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val monthlyStats: StateFlow<MonthlyStats> = transactions.map { list ->
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        val monthStr = String.format("%02d", currentMonth)
        val yearStr = currentYear.toString()
        
        val monthlyList = list.filter { it.monthStr == monthStr && it.yearStr == yearStr }
        val income = monthlyList.filter { it.type == Transaction.TYPE_INCOME }.sumOf { it.amount }
        val expense = monthlyList.filter { it.type == Transaction.TYPE_EXPENSE }.sumOf { it.amount }
        
        MonthlyStats(income, expense, income - expense)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthlyStats())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun addTransaction(
        amount: Double,
        type: String,
        category: String,
        date: Timestamp,
        note: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val cal = Calendar.getInstance()
            cal.time = date.toDate()
            val monthStr = String.format("%02d", cal.get(Calendar.MONTH) + 1)
            val yearStr = cal.get(Calendar.YEAR).toString()

            val transaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                date = date,
                monthStr = monthStr,
                yearStr = yearStr,
                note = note
            )
            repository.addTransaction(transaction)
            _isLoading.value = false
        }
    }

    private fun formatDate(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val date = timestamp.toDate()
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()
        calendar.time = date
        
        return when {
            isSameDay(calendar, today) -> "Today - ${sdf.format(date)}"
            else -> sdf.format(date)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    data class MonthlyStats(
        val totalIncome: Double = 0.0,
        val totalExpense: Double = 0.0,
        val netBalance: Double = 0.0
    )
}
