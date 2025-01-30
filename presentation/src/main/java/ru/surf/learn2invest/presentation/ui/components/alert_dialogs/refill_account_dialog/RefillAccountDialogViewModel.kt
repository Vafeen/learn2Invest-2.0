package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.refill_account_dialog

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.surf.learn2invest.domain.ProfileManager
import javax.inject.Inject


@HiltViewModel
class RefillAccountDialogViewModel @Inject constructor(
    private val profileManager: ProfileManager
) :
    ViewModel() {
    val profileFlow = profileManager.profileFlow
    private val _enteredBalanceFLow = MutableStateFlow("")
    val enteredBalanceFLow = _enteredBalanceFLow.asStateFlow()
    fun addCharToBalance(s: String) {
        _enteredBalanceFLow.update {
            "$it$s"
        }
    }

    fun removeLastCharFromBalance() {
        _enteredBalanceFLow.update {
            it.dropLast(1)
        }
    }

    fun clearBalance() {
        _enteredBalanceFLow.update {
            ""
        }
    }

    suspend fun refill(): Boolean = enteredBalanceFLow.value.toFloatOrNull().let { enteredBalance ->
        if (enteredBalance != null && enteredBalance != 0f) {
            profileManager.updateProfile {
                it.copy(fiatBalance = it.fiatBalance + enteredBalance)
            }
            true
        } else false
    }
}