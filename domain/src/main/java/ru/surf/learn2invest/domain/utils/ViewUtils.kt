package ru.surf.learn2invest.domain.utils

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

fun View.horizontalBias(): Float =
    (this.layoutParams as ConstraintLayout.LayoutParams).horizontalBias