package ru.surf.learn2invest.presentation.utils


fun Float.getWithCurrency(): String = "$this$"

fun String.getWithCurrency(): String = "$this$"
fun String.getFloatFromStringWithCurrency(): Float? = try {
    this.substring(0, this.lastIndex).toFloat()
} catch (e: Exception) {
    null
}
