package ru.surf.learn2invest.domain.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.delay

/**
 * Создает слушателя анимации для обработки событий анимации.
 *
 * @param onAnimationStart Функция, вызываемая при начале анимации.
 * @param onAnimationEnd Функция, вызываемая по окончании анимации.
 * @param onAnimationCancel Функция, вызываемая при отмене анимации.
 * @param onAnimationRepeat Функция, вызываемая при повторении анимации.
 */
fun animatorListener(
    onAnimationStart: ((animation: Animator) -> Unit)? = null,
    onAnimationEnd: ((animation: Animator) -> Unit)? = null,
    onAnimationCancel: ((animation: Animator) -> Unit)? = null,
    onAnimationRepeat: ((animation: Animator) -> Unit)? = null,
) = object : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator) {
        onAnimationStart?.invoke(animation)
    }

    override fun onAnimationEnd(animation: Animator) {
        onAnimationEnd?.invoke(animation)
    }

    override fun onAnimationCancel(animation: Animator) {
        onAnimationCancel?.invoke(animation)
    }

    override fun onAnimationRepeat(animation: Animator) {
        onAnimationRepeat?.invoke(animation)
    }
}

/**
 * Анимирует поворот и изменение прозрачности представления при нажатии.
 */
fun View.tapOn() {
    val duration = 500L

    // Анимация поворота
    val rotating = ValueAnimator.ofFloat(0f, 360f).also {
        it.duration = duration
        it.addUpdateListener { animator ->
            val rotateValue = animator.animatedValue as Float
            this.rotation = rotateValue
        }
    }

    // Анимация изменения прозрачности фона
    val flexBackground = ValueAnimator.ofFloat(1f, 0f, 1f).also {
        it.duration = duration
        it.addUpdateListener { animator ->
            val rotateValue = animator.animatedValue as Float
            this.alpha = rotateValue
        }
    }

    // Запуск анимаций одновременно
    AnimatorSet().apply {
        playTogether(rotating, flexBackground)
    }.start()
}

/**
 * Метод анимирования движения точки на [SignINActivityActions][ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions] к центру.
 *
 * @param truePIN Указывает, является ли PIN-код верным.
 * @param needReturn Указывает, нужно ли вернуть точки на свои места после анимации.
 * @param lifecycleScope Область видимости для выполнения асинхронных операций.
 * @param doAfter Callback для выполнения действий после завершения анимации.
 */
fun ImageView.gotoCenter(
    truePIN: Boolean,
    needReturn: Boolean,
    lifecycleScope: LifecycleCoroutineScope,
    doAfter: () -> Unit = {},
) {
    val home = (this.layoutParams as ConstraintLayout.LayoutParams).horizontalBias

    // Анимация перемещения к центру
    val gotoCenter = ValueAnimator.ofFloat(home, 0.5f).also {
        it.duration = 300
        it.addUpdateListener { animator ->
            val biasValue = animator.animatedValue as Float
            val params = this.layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = biasValue
            this.layoutParams = params
        }
    }

    // Анимация возврата на исходную позицию
    val goPoDomam = ValueAnimator.ofFloat(0.5f, home).also {
        it.duration = 300
        it.addUpdateListener { animator ->
            val biasValue = animator.animatedValue as Float
            val params = this.layoutParams as ConstraintLayout.LayoutParams
            params.horizontalBias = biasValue
            this.layoutParams = params
        }
    }

    goPoDomam.doOnEnd {
        doAfter() // Выполнение действий после завершения возврата на исходную позицию.
    }

    gotoCenter.start() // Запуск анимации перемещения к центру.

    gotoCenter.doOnEnd {
        lifecycleScope.launchMAIN {
            this@gotoCenter.drawable.setTint(
                if (truePIN) {
                    Color.GREEN // Установка цвета в зеленый при верном PIN-коде.
                } else {
                    Color.RED // Установка цвета в красный при неверном PIN-коде.
                }
            )
            delay(800) // Задержка перед возвратом или изменением цвета.

            if (needReturn || !truePIN) {
                goPoDomam.doOnStart {
                    this@gotoCenter.drawable.setTint(Color.WHITE) // Сброс цвета до белого перед возвратом.
                }
                goPoDomam.start() // Запуск анимации возврата на исходную позицию.
            }
        }
    }
}

/**
 * Отображает клавиатуру для текущего представления.
 */
fun View.showKeyboard() =
    ViewCompat.getWindowInsetsController(this)?.show(WindowInsetsCompat.Type.ime())

/**
 * Скрывает клавиатуру для указанной активности.
 *
 * @param activity Активность, в контексте которой скрывается клавиатура.
 */
fun View.hideKeyboard(activity: Activity) =
    (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )

