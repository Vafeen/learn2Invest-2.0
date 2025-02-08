package ru.surf.learn2invest.presentation.ui.components.screens.trading_password

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import ru.surf.learn2invest.domain.cryptography.usecase.GetHashedPasswordUseCase
import ru.surf.learn2invest.domain.cryptography.usecase.VerifyTradingPasswordUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.presentation.utils.isThisContains3NumbersIfIsNotEmpty
import ru.surf.learn2invest.presentation.utils.isThisContainsSequenceIfIsNotEmpty
import javax.inject.Inject

/**
 * ViewModel для [TradingPasswordActivity][ru.surf.learn2invest.presentation.ui.components.screens.trading_password.TradingPasswordActivity].
 *
 * @param profileManager Менеджер профиля для управления данными профиля пользователя.
 * @param getHashedPasswordUseCase Используемый для получения хэшированного пароля.
 * @param verifyTradingPasswordUseCase Используемый для проверки торгового пароля.
 * @param context Контекст приложения.
 */
@HiltViewModel
internal class TradingPasswordActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val getHashedPasswordUseCase: GetHashedPasswordUseCase,
    private val verifyTradingPasswordUseCase: VerifyTradingPasswordUseCase,
    @ApplicationContext val context: Context
) : ViewModel() {

    // Поток данных профиля пользователя.
    val profileFlow = profileManager.profileFlow

    // Потоки для хранения введенных паролей.
    private val _lastPasswordFlow = MutableStateFlow("")
    private val _tradingPasswordFlow = MutableStateFlow("")
    private val _confirmPasswordFlow = MutableStateFlow("")

    // Объединенный поток состояния правил на основе введенных паролей.
    val ruleStateFlow = combine(
        _lastPasswordFlow,
        _tradingPasswordFlow,
        _confirmPasswordFlow
    ) { lastPassword, tradingPassword, confirmPassword ->
        createRuleSetByFlows(lastPassword, tradingPassword, confirmPassword)
    }

    /**
     * Создает набор правил на основе введенных паролей.
     *
     * @param lastPassword Введенный последний пароль.
     * @param tradingPassword Введенный торговый пароль.
     * @param confirmPassword Подтвержденный пароль.
     * @return Карта состояний правил с ключами из [RuleStateKey].
     */
    private fun createRuleSetByFlows(
        lastPassword: String,
        tradingPassword: String,
        confirmPassword: String
    ): Map<RuleStateKey, Boolean> = mapOf(
        RuleStateKey.RULE_1 to (tradingPassword.length >= 6),
        RuleStateKey.RULE_2 to !tradingPassword.isThisContains3NumbersIfIsNotEmpty(),
        RuleStateKey.RULE_3 to !tradingPassword.isThisContainsSequenceIfIsNotEmpty(),
        RuleStateKey.RULE_4 to (tradingPassword == confirmPassword && tradingPassword.isNotEmpty()),
        RuleStateKey.RULE_5 to (verifyTradingPasswordUseCase.invoke(
            profileFlow.value,
            lastPassword
        ))
    )

    // Поток состояния кнопки действия на основе состояния правил.
    val mainButtonActionFlow = ruleStateFlow.map { ruleState ->
        val action = actionFlow.value
        if (action != null) {
            when (action) {
                TradingPasswordActivityActions.CreateTradingPassword -> MainActionButtonState(
                    text = context.getString(TradingPasswordActivityActions.ChangeTradingPassword.resName),
                    isVisible = ruleState[RuleStateKey.RULE_1] == true &&
                            ruleState[RuleStateKey.RULE_2] == true &&
                            ruleState[RuleStateKey.RULE_3] == true &&
                            ruleState[RuleStateKey.RULE_4] == true
                )

                TradingPasswordActivityActions.ChangeTradingPassword -> MainActionButtonState(
                    text = context.getString(TradingPasswordActivityActions.ChangeTradingPassword.resName),
                    isVisible = ruleState[RuleStateKey.RULE_1] == true &&
                            ruleState[RuleStateKey.RULE_2] == true &&
                            ruleState[RuleStateKey.RULE_3] == true &&
                            ruleState[RuleStateKey.RULE_4] == true &&
                            ruleState[RuleStateKey.RULE_5] == true
                )

                TradingPasswordActivityActions.RemoveTradingPassword -> MainActionButtonState(
                    text = context.getString(TradingPasswordActivityActions.RemoveTradingPassword.resName),
                    isVisible = ruleState[RuleStateKey.RULE_4] == true
                )
            }
        } else MainActionButtonState()
    }

    private val _actionFlow = MutableStateFlow<TradingPasswordActivityActions?>(null)

    // Поток действий активности.
    val actionFlow: StateFlow<TradingPasswordActivityActions?> = _actionFlow.asStateFlow()

    /**
     * Обновляет торговый пароль в профиле пользователя.
     */
    suspend fun updateTradingPassword() {
        updateProfile {
            if (actionFlow.value == TradingPasswordActivityActions.CreateTradingPassword ||
                actionFlow.value == TradingPasswordActivityActions.ChangeTradingPassword
            ) {
                it.copy(
                    tradingPasswordHash = getHashedPasswordUseCase.invoke(
                        it,
                        _tradingPasswordFlow.value
                    )
                )
            } else it.copy(tradingPasswordHash = null)
        }
    }

    /**
     * Обновляет последний введенный пароль.
     *
     * @param password Новый последний пароль.
     */
    fun updateLastPassword(password: String) {
        _lastPasswordFlow.update { password }
    }

    /**
     * Обновляет введенный торговый пароль.
     *
     * @param password Новый торговый пароль.
     */
    fun updatePassword(password: String) {
        _tradingPasswordFlow.update { password }
    }

    /**
     * Обновляет подтвержденный пароль.
     *
     * @param password Новый подтвержденный пароль.
     */
    fun updateConfirmPassword(password: String) {
        _confirmPasswordFlow.update { password }
    }

    /**
     * Обновляет профиль пользователя с использованием переданной функции обновления.
     *
     * @param updating Функция для обновления профиля.
     */
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    /**
     * Получает drawable ресурс по идентификатору ресурса.
     *
     * @param res Идентификатор ресурса drawable.
     * @return Drawable ресурс.
     */
    fun getDrawableRes(@DrawableRes res: Int) = ContextCompat.getDrawable(context, res)

    /**
     * Инициализирует действие в зависимости от переданного intentAction.
     *
     * @param intentAction Действие, переданное через Intent при запуске активности.
     */
    fun initAction(intentAction: String) {
        when (intentAction) {
            TradingPasswordActivityActions.ChangeTradingPassword.action -> {
                _actionFlow.value = TradingPasswordActivityActions.ChangeTradingPassword
            }

            TradingPasswordActivityActions.RemoveTradingPassword.action -> {
                _actionFlow.value = TradingPasswordActivityActions.RemoveTradingPassword
            }

            TradingPasswordActivityActions.CreateTradingPassword.action -> {
                _actionFlow.value = TradingPasswordActivityActions.CreateTradingPassword
            }

            else -> throw Exception("action is not defined or not is TradingPasswordActivityActions")
        }
    }
}