package ru.surf.learn2invest.domain.animator

import android.animation.Animator
import android.view.View

interface CustomAnimator {
    fun animateViewAlpha(
        view: View,
        duration: Long,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null,
        vararg values: Float,
    )

    fun animateHorizontalBias(
        view: View,
        duration: Long,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null,
        vararg values: Float,
    ): Animator
}