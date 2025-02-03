package ru.surf.learn2invest.presentation.utils

import android.content.Context
import ru.surf.learn2invest.presentation.R


fun Float.getWithCurrency(): String = "$this$"

fun String.getWithCurrency(): String = "$this$"
fun String.getFloatFromStringWithCurrency(): Float? = try {
    this.substring(0, this.lastIndex).toFloat()
} catch (e: Exception) {
    null
}

fun String.getWithPCS(context: Context): String = "$this ${context.getString(R.string.pcs)}"
