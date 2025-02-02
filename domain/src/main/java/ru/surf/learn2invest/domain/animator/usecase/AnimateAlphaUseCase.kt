package ru.surf.learn2invest.domain.animator.usecase

import android.view.View
import ru.surf.learn2invest.domain.animator.CustomAnimator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimateAlphaUseCase @Inject constructor(private val customAnimator: CustomAnimator) {
    operator fun invoke(
        view: View,
        duration: Long,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null,
        values: FloatArray,
    ) = customAnimator.animateViewAlpha(view, duration, onStart, onEnd, onCancel, onRepeat, *values)
}