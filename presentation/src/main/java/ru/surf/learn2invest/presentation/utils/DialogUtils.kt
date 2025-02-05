package ru.surf.learn2invest.presentation.utils

import androidx.fragment.app.FragmentManager
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.CustomBottomSheetDialog

internal fun CustomBottomSheetDialog.showDialog(fragmentManager: FragmentManager) =
    show(fragmentManager, this.dialogTag)
