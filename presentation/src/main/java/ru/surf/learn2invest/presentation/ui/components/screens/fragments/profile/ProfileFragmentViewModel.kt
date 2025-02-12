package ru.surf.learn2invest.presentation.ui.components.screens.fragments.profile

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.domain.database.usecase.ClearAppDatabaseUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.domain.utils.withContextIO
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.presentation.ui.components.screens.trading_password.TradingPasswordActivity
import ru.surf.learn2invest.presentation.ui.components.screens.trading_password.TradingPasswordActivityActions
import javax.inject.Inject

/**
 * ViewModel для управления состоянием профиля пользователя в приложении.
 * Отвечает за обновление профиля, смену пароля для торговли, работу с биометрической аутентификацией,
 * сброс статистики и другие операции, связанные с профилем пользователя.
 *
 * @param profileManager Менеджер профиля пользователя, который предоставляет доступ к данным
 *                       профиля и позволяет обновлять их.
 * @param clearAppDatabaseUseCase UseCase для очистки базы данных приложения.
 * @param fingerprintAuthenticator Аутентификатор для работы с биометрией пользователя.
 */
@HiltViewModel
internal class ProfileFragmentViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val clearAppDatabaseUseCase: ClearAppDatabaseUseCase,
    private val fingerprintAuthenticator: FingerprintAuthenticator,
) : ViewModel() {

    // Поток данных профиля
    val profileFlow = profileManager.profileFlow

    /**
     * Обновляет профиль пользователя с помощью переданной функции.
     *
     * @param updating Функция для обновления данных профиля.
     */
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    /**
     * Запускает активность для изменения пароля для торговли.
     *
     * @param context Контекст, необходимый для запуска активности.
     * @param tradingPasswordActivityActions Действие для активности изменения пароля.
     */
    private fun startIntentWithAction(
        context: Context,
        tradingPasswordActivityActions: TradingPasswordActivityActions,
    ) = context.startActivity(
        Intent(
            context,
            TradingPasswordActivity::class.java
        ).also { it.action = tradingPasswordActivityActions.action })


    /**
     * Сбрасывает статистику профиля пользователя, обнуляя балансы.
     *
     * @param context Контекст для отображения уведомления.
     */
    suspend fun resetStats(context: Context) {
        val savedProfile = profileFlow.value.copy(
            fiatBalance = 0f,
            assetBalance = 0f
        )
        withContextIO {
            clearAppDatabaseUseCase()
            updateProfile {
                savedProfile
            }
        }
        Toast.makeText(context, context.getString(R.string.stat_reset), Toast.LENGTH_LONG).show()
    }

    /**
     * Изменяет настройку подтверждения транзакции, в зависимости от наличия пароля для торговли.
     *
     * @param context Контекст для запуска активности.
     */
    fun changeTransactionConfirmation(context: Context) = startIntentWithAction(
        context, if (profileFlow.value.tradingPasswordHash == null)
            TradingPasswordActivityActions.CreateTradingPassword
        else TradingPasswordActivityActions.RemoveTradingPassword
    )

    /**
     * Запускает активность для изменения пароля для торговли.
     *
     * @param context Контекст для запуска активности.
     */
    fun changeTradingPassword(context: Context) =
        startIntentWithAction(context, TradingPasswordActivityActions.ChangeTradingPassword)

    /**
     * Переключает настройку биометрической аутентификации для пользователя.
     *
     * @param activity Активность, из которой будет вызвана биометрическая аутентификация.
     */
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

    /**
     * Запускает активность для изменения PIN-кода.
     *
     * @param context Контекст для запуска активности.
     */
    fun changePIN(context: Context) {
        context.startActivity(Intent(context, SignInActivity::class.java).also {
            it.action = SignINActivityActions.ChangingPIN.action
        })
    }

    /**
     * Проверяет, доступна ли биометрическая аутентификация на устройстве.
     *
     * @param activity Активность, в контексте которой будет проверяться доступность биометрии.
     * @return true, если биометрическая аутентификация доступна, иначе false.
     */
    fun isBiometricAvailable(activity: AppCompatActivity): Boolean =
        fingerprintAuthenticator.isBiometricAvailable(activity)
}
