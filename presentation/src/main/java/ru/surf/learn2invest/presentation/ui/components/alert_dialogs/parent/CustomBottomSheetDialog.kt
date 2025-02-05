package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent

import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Класс для удобной реализации BottomSheetDialogs
 */
internal abstract class CustomBottomSheetDialog : BottomSheetDialogFragment() {
    abstract val dialogTag: String
    fun showDialog(fragmentManager: FragmentManager) = show(fragmentManager, this.dialogTag)
}