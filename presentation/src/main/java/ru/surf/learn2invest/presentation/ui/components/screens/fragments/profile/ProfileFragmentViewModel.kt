package ru.surf.learn2invest.presentation.ui.components.screens.fragments.profile

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.ProfileManager
import ru.surf.learn2invest.domain.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.domain.database.usecase.ClearAppDatabaseUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.presentation.ui.components.screens.trading_password.TradingPasswordActivity
import ru.surf.learn2invest.presentation.ui.components.screens.trading_password.TradingPasswordActivityActions
import ru.surf.learn2invest.presentation.ui.main.MainActivity
import ru.surf.learn2invest.presentation.utils.launchIO
import ru.surf.learn2invest.presentation.utils.withContextIO
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val clearAppDatabaseUseCase: ClearAppDatabaseUseCase,
    private val fingerprintAuthenticator: FingerprintAuthenticator
) : ViewModel() {
    val profileFlow = profileManager.profileFlow
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    private fun startIntentWithAction(
        context: Context,
        tradingPasswordActivityActions: TradingPasswordActivityActions
    ) = context.startActivity(
        Intent(
            context,
            TradingPasswordActivity::class.java
        ).also { it.action = tradingPasswordActivityActions.action })

    suspend fun deleteProfile(activity: AppCompatActivity) {
        activity.finish()
        withContextIO {
            clearDB()
        }
        activity.startActivity(
            Intent(
                activity,
                MainActivity::class.java
            )
        )
    }

    suspend fun resetStats(context: Context) {
        val savedProfile = profileFlow.value.copy(
            fiatBalance = 0f,
            assetBalance = 0f
        )
        withContextIO {
            clearDB()
            updateProfile {
                savedProfile
            }
        }
        Toast.makeText(context, context.getString(R.string.stat_reset), Toast.LENGTH_LONG).show()
    }

    fun changeTransactionConfirmation(context: Context) = startIntentWithAction(
        context, if (profileFlow.value.tradingPasswordHash == null)
            TradingPasswordActivityActions.CreateTradingPassword
        else TradingPasswordActivityActions.RemoveTradingPassword
    )


    fun changeTradingPassword(context: Context) =
        startIntentWithAction(context, TradingPasswordActivityActions.ChangeTradingPassword)

    fun biometryBtnSwitch(activity: AppCompatActivity) {
        val profile = profileFlow.value
        if (profile.biometry) {
            viewModelScope.launchIO {
                updateProfile {
                    profile.copy(biometry = false)
                }
            }
        } else {
            fingerprintAuthenticator.setSuccessCallback {
                viewModelScope.launchIO {
                    updateProfile { profile.copy(biometry = true) }
                }
            }.setDesignBottomSheet(
                title = activity.getString(R.string.biometry),
                cancelText = activity.getString(R.string.cancel),
            ).auth(coroutineScope = viewModelScope, activity = activity)
        }
    }

    private suspend fun clearDB() = clearAppDatabaseUseCase()

    fun changePIN(context: Context) {
        context.startActivity(Intent(context, SignInActivity::class.java).also {
            it.action = SignINActivityActions.ChangingPIN.action
        })
    }

    fun isBiometricAvailable(activity: AppCompatActivity): Boolean =
        fingerprintAuthenticator.isBiometricAvailable(activity)
}