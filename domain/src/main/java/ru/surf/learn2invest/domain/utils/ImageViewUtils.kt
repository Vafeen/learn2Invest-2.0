package ru.surf.learn2invest.domain.utils

import android.widget.ImageView
import androidx.annotation.ColorInt

fun changeDrawableColor(@ColorInt color: Int, imageViews: Array<ImageView>) {
    imageViews.forEach {
        it.drawable.setTint(color)
    }
}