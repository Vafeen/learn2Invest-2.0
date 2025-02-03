package ru.surf.learn2invest.domain.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

