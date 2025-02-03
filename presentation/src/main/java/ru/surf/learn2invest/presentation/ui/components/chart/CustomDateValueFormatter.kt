package ru.surf.learn2invest.presentation.ui.components.chart

import com.github.mikephil.charting.charts.LineChart

/**
 * Интерфейс для кастомных классов отображения даты на графике
 */
internal interface CustomDateValueFormatter {
    fun getFormattedValue(value: Float, chart: LineChart): String
}