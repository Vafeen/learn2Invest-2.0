package ru.surf.learn2invest.ui.components.screens.trading_password

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.R
import ru.surf.learn2invest.domain.ProfileManager
import ru.surf.learn2invest.domain.cryptography.PasswordHasher
import ru.surf.learn2invest.domain.cryptography.usecase.VerifyTradingPasswordUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

@HiltViewModel
class TradingPasswordActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val passwordHasher: PasswordHasher,
    val verifyTradingPasswordUseCase: VerifyTradingPasswordUseCase,
) :
    ViewModel() {
    lateinit var action: TradingPasswordActivityActions
    lateinit var actionName: String
    lateinit var mainButtonAction: String
    val profileFlow = profileManager.profileFlow
    suspend fun saveTradingPassword(password: String) {
        updateProfile {
            if (action == TradingPasswordActivityActions.CreateTradingPassword ||
                action == TradingPasswordActivityActions.ChangeTradingPassword
            ) {
                it.copy(
                    tradingPasswordHash = passwordHasher.passwordToHash(
                        firstName = it.firstName,
                        lastName = it.lastName,
                        password = password
                    )
                )
            } else it.copy(tradingPasswordHash = null)
        }

    }

    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    fun initAction(intentAction: String, context: Context): Boolean {
        var isOk = true
        action = when (intentAction) {
            TradingPasswordActivityActions.ChangeTradingPassword.action -> {
                apply {
                    actionName = ContextCompat.getString(
                        context,
                        R.string.change_trading_password
                    )
                    mainButtonAction =
                        ContextCompat.getString(context, R.string.change)
                }
                TradingPasswordActivityActions.ChangeTradingPassword
            }

            TradingPasswordActivityActions.RemoveTradingPassword.action -> {
                apply {
                    actionName = ContextCompat.getString(
                        context,
                        R.string.remove_trading_password
                    )
                    mainButtonAction =
                        ContextCompat.getString(context, R.string.remove)
                }
                TradingPasswordActivityActions.RemoveTradingPassword
            }

            TradingPasswordActivityActions.CreateTradingPassword.action -> {
                actionName = ContextCompat.getString(
                    context,
                    R.string.create_trading_password
                )
                mainButtonAction =
                    ContextCompat.getString(context, R.string.create)

                isOk = intentAction == TradingPasswordActivityActions.CreateTradingPassword.action

                TradingPasswordActivityActions.CreateTradingPassword
            }

            else -> {
                // finish if action is not defined

                TradingPasswordActivityActions.CreateTradingPassword
            }
        }
        return isOk
    }
}