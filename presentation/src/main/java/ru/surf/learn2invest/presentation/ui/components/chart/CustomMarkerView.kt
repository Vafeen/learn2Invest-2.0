package ru.surf.learn2invest.presentation.ui.components.chart

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ru.surf.learn2invest.presentation.R

/**
 * Кастомный маркер для отображения цены на графике при клике.
 *
 * Этот класс используется для отображения кастомного маркера на графике при взаимодействии пользователя
 * с точками данных. Он позволяет отображать цену (или другие данные) в виде текста и позиционировать маркер
 * по отношению к точке на графике.
 *
 * @param context Контекст, который используется для инициализации маркера.
 * @param layoutResource Ресурс макета, который используется для отображения маркера.
 */
internal class CustomMarkerView(
    context: Context,
    layoutResource: Int
) : MarkerView(context, layoutResource) {

    // Ссылка на TextView для отображения данных
    private var tvContent: TextView = findViewById(R.id.tvContent)

    /**
     * Вызывается каждый раз при перерисовке маркера. Можно использовать для обновления контента.
     * В данном случае, отображается цена (y-значение) на маркере.
     *
     * @param e Текущая точка данных (Entry), с которой связан маркер.
     * @param highlight Объект, содержащий информацию о выделенной точке.
     */
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        // Отображаем цену (y-значение) в TextView
        tvContent.text = e?.y?.toBigDecimal()?.toPlainString() ?: ""
        // Выполняем позиционирование маркера
        super.refreshContent(e, highlight)
    }

    private var mOffset: MPPointF? = null

    /**
     * Возвращает смещение маркера относительно его положения на графике.
     * В данном случае, маркер центрируется относительно выбранной точки.
     *
     * @return Смещение маркера, необходимое для его правильного позиционирования.
     */
    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // Центрируем маркер по горизонтали и располагаем его выше точки
            mOffset = MPPointF(-(width / 2f), -height.toFloat())
        }
        return mOffset ?: MPPointF()
    }
}
