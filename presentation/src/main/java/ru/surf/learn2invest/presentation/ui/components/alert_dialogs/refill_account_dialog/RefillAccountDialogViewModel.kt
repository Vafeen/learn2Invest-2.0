package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.refill_account_dialog

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.surf.learn2invest.domain.services.ProfileManager
import javax.inject.Inject

/**
 * ViewModel для управления логикой пополнения баланса.
 *
 * @property profileManager Менеджер профиля, используемый для обновления данных пользователя.
 */
@HiltViewModel
internal class RefillAccountDialogViewModel @Inject constructor(
    private val profileManager: ProfileManager,
) : ViewModel() {

    /** Поток данных профиля пользователя. */
    val profileFlow = profileManager.profileFlow

    private val _enteredBalanceFLow = MutableStateFlow("")

    /** Поток введённого пользователем значения баланса. */
    val enteredBalanceFLow = _enteredBalanceFLow.asStateFlow()

    /**
     * Добавляет символ к текущей строке введённого баланса.
     *
     * @param s Символ, который нужно добавить.
     */
    fun addCharToBalance(s: String) {
        _enteredBalanceFLow.update { it + s }
    }

    /**
     * Удаляет последний введённый символ из строки баланса.
     */
    fun removeLastCharFromBalance() {
        _enteredBalanceFLow.update { it.dropLast(1) }
    }

    /**
     * Очищает введённый баланс.
     */
    fun clearBalance() {
        _enteredBalanceFLow.update { "" }
    }

    /**
     * Пополняет баланс, если введённое значение является корректным числом больше нуля.
     *
     * @return `true`, если пополнение успешно, иначе `false`.
     */
    suspend fun refill(): Boolean {
        val enteredBalance = enteredBalanceFLow.value.toFloatOrNull()
        return if (enteredBalance != null && enteredBalance > 0) {
            profileManager.updateProfile {
                it.copy(fiatBalance = it.fiatBalance + enteredBalance)
            }
            true
        } else {
            false
        }
    }
}
