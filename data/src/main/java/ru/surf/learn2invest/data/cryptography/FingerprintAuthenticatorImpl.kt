package ru.surf.learn2invest.data.cryptography

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.cryptography.FingerprintAuthenticator
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация интерфейса FingerprintAuthenticator для аутентификации пользователя с помощью отпечатка пальца.
 */
@Singleton
internal class FingerprintAuthenticatorImpl @Inject constructor() : FingerprintAuthenticator {
    /**
     * Проверяет, доступно ли биометрическое аппаратное обеспечение на устройстве.
     *
     * @param activity Активити, в которой выполняется проверка.
     * @return True, если биометрическое обеспечение доступно, false иначе.
     */
    override fun isBiometricAvailable(activity: AppCompatActivity): Boolean {
        return BiometricManager.from(activity)
            .canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Устанавливает callback-функцию, вызываемую при успешной аутентификации.
     *
     * @param function Callback-функция, которая будет вызвана при успешной аутентификации.
     * @return Экземпляр FingerprintAuthenticator для цепного вызова методов.
     */
    override fun setSuccessCallback(function: () -> Unit): FingerprintAuthenticator {
        this.successCallBack = function
        return this
    }

    /**
     * Устанавливает callback-функцию, вызываемую при неуспешной аутентификации.
     *
     * @param function Callback-функция, которая будет вызвана при неуспешной аутентификации.
     * @return Экземпляр FingerprintAuthenticator для цепного вызова методов.
     */
    override fun setFailedCallback(function: () -> Unit): FingerprintAuthenticator {
        this.failedCallBack = function
        return this
    }

    /**
     * Устанавливает callback-функцию, вызываемую при отмене аутентификации пользователем.
     *
     * @param function Callback-функция, которая будет вызвана при отмене аутентификации.
     * @return Экземпляр FingerprintAuthenticator для цепного вызова методов.
     */
    override fun setCancelCallback(function: () -> Unit): FingerprintAuthenticator {
        this.setCancelCallback = function
        return this
    }

    /**
     * Настройка дизайна BottomSheet для аутентификации.
     *
     * @param title Название BottomSheet.
     * @param cancelText Текст кнопки отмены.
     * @return Экземпляр FingerprintAuthenticator для цепного вызова методов.
     */
    override fun setDesignBottomSheet(title: String, cancelText: String): FingerprintAuthenticator {
        titleText = title
        cancelButtonText = cancelText
        return this
    }

    /**
     * Запускает процесс аутентификации с помощью отпечатка пальца.
     *
     * @param coroutineScope CoroutineScope для запуска корутин.
     * @param activity Активити, в которой выполняется аутентификация.
     * @return Job, который можно использовать для отмены корутины.
     */
    override fun auth(
        coroutineScope: CoroutineScope,
        activity: AppCompatActivity
    ): Job {
        return coroutineScope.launch(Dispatchers.Main) {
            if (isBiometricAvailable(activity = activity)) {
                initFingerPrintAuth(activity = activity)
                checkAuthenticationFingerprint()
            }
        }
    }

    // callbacks
    private var failedCallBack: () -> Unit = {}
    private var successCallBack: () -> Unit = {}
    private var setCancelCallback: () -> Unit = {}

    // design bottom sheet
    private lateinit var titleText: String
    private lateinit var cancelButtonText: String

    // for authentication
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    /**
     * Инициализирует BiometricPrompt для аутентификации.
     *
     * @param activity Активити, в которой выполняется аутентификация.
     * @return Экземпляр FingerprintAuthenticator для цепного вызова методов.
     */
    private fun initFingerPrintAuth(activity: AppCompatActivity): FingerprintAuthenticator {
        executor = ContextCompat.getMainExecutor(activity)
        biometricPrompt =
            BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    /**
                     * Вызывается при успешной аутентификации.
                     */
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        successCallBack()
                    }

                    /**
                     * Вызывается при ошибке аутентификации.
                     */
                    override fun onAuthenticationError(
                        errorCode: Int, errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        setCancelCallback()
                    }

                    /**
                     * Вызывается при неуспешной аутентификации.
                     */
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        failedCallBack()
                    }
                })

        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(titleText)
            .setNegativeButtonText(cancelButtonText).build()

        return this
    }

    /**
     * Запускает процесс аутентификации с помощью BiometricPrompt.
     */
    private fun checkAuthenticationFingerprint() {
        biometricPrompt.authenticate(promptInfo)
    }
}