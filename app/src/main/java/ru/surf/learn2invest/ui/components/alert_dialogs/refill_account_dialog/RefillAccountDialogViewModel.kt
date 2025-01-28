package ru.surf.learn2invest.ui.components.alert_dialogs.refill_account_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.ProfileManager
import javax.inject.Inject


@HiltViewModel
class RefillAccountDialogViewModel @Inject constructor(
    private val profileManager: ProfileManager
) :
    ViewModel() {
    val profileFlow = profileManager.profileFlow
    var enteredBalanceF: Float = 0f

    fun refill() {
        viewModelScope.launch(Dispatchers.IO) {
            profileManager.updateProfile {
                it.copy(fiatBalance = it.fiatBalance + enteredBalanceF)
            }
        }
    }
}