package com.example.finmate.data.model

import com.google.firebase.Timestamp

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val type: String = "EXPENSE", // "EXPENSE" or "INCOME"
    val category: String = "",
    val date: Timestamp = Timestamp.now(),
    val monthStr: String = "",
    val yearStr: String = "",
    val note: String = ""
) {
    companion object {
        const val TYPE_EXPENSE = "EXPENSE"
        const val TYPE_INCOME = "INCOME"
    }
}
