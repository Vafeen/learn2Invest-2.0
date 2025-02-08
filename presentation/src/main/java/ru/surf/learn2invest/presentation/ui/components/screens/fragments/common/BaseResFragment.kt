package ru.surf.learn2invest.presentation.ui.components.screens.fragments.common

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment

internal open class BaseResFragment : Fragment() {
    protected fun getColorRes(@ColorRes resId: Int) = requireContext().getColor(resId)
    protected fun getStringRes(@StringRes resId: Int) = requireContext().getString(resId)
    protected fun getDrawableRes(@DrawableRes resId: Int) =
        AppCompatResources.getDrawable(requireContext(), resId)
}