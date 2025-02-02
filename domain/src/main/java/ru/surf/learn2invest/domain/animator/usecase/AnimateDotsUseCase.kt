package ru.surf.learn2invest.domain.animator.usecase

import android.animation.AnimatorSet
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import ru.surf.learn2invest.domain.animator.CustomAnimator
import ru.surf.learn2invest.domain.utils.changeDrawableColor
import ru.surf.learn2invest.domain.utils.horizontalBias
import javax.inject.Inject

/**
 * Метод анимирования движения точки на [SignINActivityActions][ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions] к центру.
 *
 * @param truePIN Указывает, является ли PIN-код верным.
 * @param needReturn Указывает, нужно ли вернуть точки на свои места после анимации.
 * @param lifecycleScope Область видимости для выполнения асинхронных операций.
 * @param doAfter Callback для выполнения действий после завершения анимации.
 */
class AnimateDotsUseCase @Inject constructor(private val customAnimator: CustomAnimator) {

    operator fun invoke(
        dot1: ImageView,
        dot2: ImageView,
        dot3: ImageView,
        dot4: ImageView,
        needReturn: Boolean,
        truePIN: Boolean,
        onEnd: () -> Unit,
    ) {
        val aimBias = 0.5f
        val duration = 300L
        val homeBiasDot1 = dot1.horizontalBias()
        val homeBiasDot2 = dot2.horizontalBias()
        val homeBiasDot3 = dot3.horizontalBias()
        val homeBiasDot4 = dot4.horizontalBias()
        // Второй AnimatorSet для возврата к исходным смещениям
        val animatorSet2 = AnimatorSet().apply {
            playTogether(
                customAnimator.animateHorizontalBias(
                    view = dot1,
                    duration = duration,
                    values = floatArrayOf(aimBias, homeBiasDot1)
                ),
                customAnimator.animateHorizontalBias(
                    view = dot2,
                    duration = duration,
                    values = floatArrayOf(aimBias, homeBiasDot2)
                ),
                customAnimator.animateHorizontalBias(
                    view = dot3,
                    duration = duration,
                    values = floatArrayOf(aimBias, homeBiasDot3)
                ),
                customAnimator.animateHorizontalBias(
                    view = dot4,
                    duration = duration,
                    values = floatArrayOf(aimBias, homeBiasDot4)
                )
            )
            doOnStart {
                changeDrawableColor(Color.WHITE, arrayOf(dot1, dot2, dot3, dot4))
            }
            doOnEnd {
                onEnd()
            }
        }
        AnimatorSet().apply {
            playTogether(
                customAnimator.animateHorizontalBias(
                    view = dot1,
                    duration = duration,
                    values = floatArrayOf(homeBiasDot1, aimBias),
                ),
                customAnimator.animateHorizontalBias(
                    view = dot2,
                    duration = duration,
                    values = floatArrayOf(homeBiasDot2, aimBias),
                ),
                customAnimator.animateHorizontalBias(
                    view = dot3,
                    duration = duration,
                    values = floatArrayOf(homeBiasDot3, aimBias),
                ),
                customAnimator.animateHorizontalBias(
                    view = dot4,
                    duration = duration,
                    values = floatArrayOf(homeBiasDot4, aimBias),
                ),
            )
            doOnEnd {
                changeDrawableColor(
                    if (truePIN) {
                        Color.GREEN // Установка цвета в зеленый при верном PIN-коде.
                    } else {
                        Color.RED // Установка цвета в красный при неверном PIN-коде.
                    }, arrayOf(dot1, dot2, dot3, dot4)
                )
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        if (needReturn) {
                            animatorSet2.start()
                        } else {
                            onEnd()
                        }
                    },
                    800,
                )

            }
        }.start()
    }
}