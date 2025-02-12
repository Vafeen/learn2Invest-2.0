package ru.surf.learn2invest.presentation.ui.components.chart

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.surf.learn2invest.presentation.R

/**
 * Класс, который отвечает за настройку и обновление данных графика.
 *
 * Этот класс инкапсулирует логику настройки графика с использованием библиотеки MPAndroidChart. Он
 * позволяет задавать внешний вид графика, а также обновлять данные, отображаемые на графике, в
 * зависимости от новых данных.
 *
 * @param context Контекст для доступа к ресурсам и настройкам приложения.
 * @param dateFormatterStrategy Стратегия форматирования дат для оси X графика.
 */
internal class LineChartHelper(
    private val context: Context,
    private val dateFormatterStrategy: CustomDateValueFormatter
) {
    private lateinit var chart: LineChart

    /**
     * Настройка графика с заданными параметрами.
     *
     * Этот метод инициализирует график, устанавливая настройки для отображения данных, а также
     * форматирования оси X, оси Y и других параметров визуализации.
     *
     * @param lineChart Экземпляр графика, который будет настроен.
     */
    fun setupChart(lineChart: LineChart) {
        this.chart = lineChart
        lineChart.apply {
            setExtraOffsets(30f, 0f, 30f, 10f)
            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = false
                axisMinimum = 0f
                textSize = 12f
                setDrawGridLines(false)
            }
            xAxis.apply {
                axisMinimum = -0.3f
                axisMaximum = 6.3f
                isGranularityEnabled = true
                granularity = 1f
                textSize = 10f
                textColor = ContextCompat.getColor(
                    context,
                    if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                        R.color.white
                    } else {
                        R.color.black
                    }
                )
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return dateFormatterStrategy.getFormattedValue(value, chart)
                    }
                }
                typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium)
            }
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawMarkers(true)
            marker = CustomMarkerView(context, R.layout.chart_marker)
            description = null
            legend.isEnabled = false
        }
    }

    /**
     * Обновление данных графика с новыми точками.
     *
     * Этот метод обновляет данные на графике, изменяя диапазоны осей X и Y в зависимости от
     * новых данных. Он также применяет стиль к графику, учитывая ночной или дневной режим.
     *
     * @param data Новый список данных (точек), который будет отображаться на графике.
     */
    fun updateData(data: List<Entry>) {
        val lineDataSet = LineDataSet(data, "List")
        val lineData = LineData(lineDataSet)
        styleLineDataSet(lineDataSet)

        chart.apply {
            if (data.size > 1) {
                xAxis.axisMinimum = 0f
                xAxis.axisMaximum = (data.size - 1).toFloat()

                val minY = data.minByOrNull { it.y }?.y ?: 0f
                val maxY = data.maxByOrNull { it.y }?.y ?: 0f

                axisLeft.axisMinimum = minY - (0.1f * (maxY - minY))
                axisLeft.axisMaximum = maxY + (0.1f * (maxY - minY))
            } else {
                xAxis.axisMinimum = -0.5f
                xAxis.axisMaximum = 0.5f
                val singleValue = data.firstOrNull()?.y ?: 0f
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = singleValue + (0.1f * singleValue)
            }

            this.data = lineData
            invalidate()
        }
    }

    /**
     * Применяет стиль к набору данных линии.
     *
     * Этот метод настраивает внешний вид линии на графике, в том числе цвет, толщину линии,
     * включение/выключение индикаторов и заполнение области под графиком.
     *
     * @param lineDataSet Набор данных линии, которому применяются стили.
     */
    private fun styleLineDataSet(lineDataSet: LineDataSet) = lineDataSet.apply {
        color = ContextCompat.getColor(
            context,
            if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                R.color.accent_background_dark
            } else {
                R.color.accent_background
            }
        )
        valueTextColor = Color.BLACK
        lineWidth = 2f
        isHighlightEnabled = true
        valueTextSize = 15f
        setDrawValues(false)
        setDrawCircles(false)
        setDrawHighlightIndicators(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawFilled(true)
        fillDrawable = ContextCompat.getDrawable(context, R.drawable.line_chart_style)
    }
}