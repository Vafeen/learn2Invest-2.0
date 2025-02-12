package ru.surf.learn2invest.presentation.ui.components.screens.fragments.common

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment

/**
 * Базовый фрагмент, предоставляющий удобные методы для получения ресурсов (цветов, строк и изображений)
 * в дочерних фрагментах. Этот фрагмент используется в качестве родительского для других фрагментов,
 * которые нуждаются в доступе к ресурсам.
 */
internal open class BaseResFragment : Fragment() {

    /**
     * Возвращает цвет по ресурсу.
     *
     * @param resId Идентификатор ресурса цвета.
     * @return Цвет в формате int.
     */
    protected fun getColorRes(@ColorRes resId: Int) = requireContext().getColor(resId)

    /**
     * Возвращает строку по ресурсу.
     *
     * @param resId Идентификатор ресурса строки.
     * @return Строка из ресурсов.
     */
    protected fun getStringRes(@StringRes resId: Int) = requireContext().getString(resId)

    /**
     * Возвращает изображение по ресурсу.
     *
     * @param resId Идентификатор ресурса изображения.
     * @return Изображение, представленное Drawable.
     */
    protected fun getDrawableRes(@DrawableRes resId: Int) =
        AppCompatResources.getDrawable(requireContext(), resId)
}
