package com.example.finmate.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.formatVND(): String {
    val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
    symbols.groupingSeparator = '.'
    val formatter = DecimalFormat("#,###", symbols)
    return "${formatter.format(this)} đ"
}

fun Long.formatVND(): String {
    return this.toDouble().formatVND()
}
