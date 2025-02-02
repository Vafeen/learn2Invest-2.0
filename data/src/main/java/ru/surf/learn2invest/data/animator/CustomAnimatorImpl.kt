package ru.surf.learn2invest.data.animator

import android.animation.ObjectAnimator
import android.view.View
import ru.surf.learn2invest.domain.animator.CustomAnimator
import ru.surf.learn2invest.domain.utils.animatorListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CustomAnimatorImpl @Inject constructor() : CustomAnimator {

    override fun animateViewAlpha(
        view: View,
        duration: Long,
        onStart: (() -> Unit)?,
        onEnd: (() -> Unit)?,
        onCancel: (() -> Unit)?,
        onRepeat: (() -> Unit)?,
        vararg values: Float,
    ) {
        ObjectAnimator.ofFloat(view, "alpha", *values).also { animator ->
            animator.duration = duration
            animator.addListener(animatorListener(onAnimationStart = {
                onStart?.invoke()
            },
                onAnimationEnd = {
                    onEnd?.invoke()
                }, onAnimationCancel = { onCancel?.invoke() }, onAnimationRepeat = {
                    onRepeat?.invoke()
                })
            )
        }.start()
    }


}