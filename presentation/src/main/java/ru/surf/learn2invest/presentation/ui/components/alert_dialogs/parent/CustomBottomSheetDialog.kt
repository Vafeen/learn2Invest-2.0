package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Класс для удобной реализации BottomSheetDialogs
 */
internal abstract class CustomBottomSheetDialog : BottomSheetDialogFragment() {
    abstract val dialogTag: String
}