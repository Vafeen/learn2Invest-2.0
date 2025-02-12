package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent

import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
/**
 * Абстрактный класс для удобного создания BottomSheetDialog.
 * Позволяет наследникам легко отображать диалог через `showDialog()`.
 */
internal abstract class CustomBottomSheetDialog : BottomSheetDialogFragment() {

    /**
     * Уникальный тег для идентификации диалогового окна в FragmentManager.
     */
    abstract val dialogTag: String

    /**
     * Показывает BottomSheetDialog, используя переданный FragmentManager.
     *
     * @param fragmentManager Менеджер фрагментов, через который будет показан диалог.
     */
    fun showDialog(fragmentManager: FragmentManager) = show(fragmentManager, dialogTag)
}
