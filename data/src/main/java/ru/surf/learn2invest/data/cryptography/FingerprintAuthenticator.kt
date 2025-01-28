package ru.surf.learn2invest.data.cryptography

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Аутентификация пользователя с помощью отпечатка пальца
 */
@Singleton
class FingerprintAuthenticator @Inject constructor() {
    /**
     * Проверка наличия биометрического аппаратного обеспечения
     */
    fun isBiometricAvailable(activity: AppCompatActivity): Boolean {
        return BiometricManager.from(activity)
            .canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Сеттер для callback в случае успешной аутентифиакции
     * @param function [callback в случае успешной аутентификации]
     */
    fun setSuccessCallback(function: () -> Unit): FingerprintAuthenticator {
        this.successCallBack = function

        return this
    }

    /**
     * Сеттер для callback в случае неуспешной аутентифиакции
     * @param function [callback ]
     */
    fun setFailedCallback(function: () -> Unit): FingerprintAuthenticator {
        this.failedCallBack = function

        return this
    }

    /**
     * Сеттер для callback в случае отмены пользователем аутентифиакции
     * @param function [callback ]
     */
    fun setCancelCallback(function: () -> Unit): FingerprintAuthenticator {
        this.setCancelCallback = function

        return this
    }

    /**
     * Настройка дизайна названия BottomSheet и кнопки отмены действия
     * @param title [название BottomSheet]
     * @param cancelText [Текст кнопки отмены]
     */
    fun setDesignBottomSheet(
        title: String,
        cancelText: String
    ): FingerprintAuthenticator {
        titleText = title
        cancelButtonText = cancelText
        return this
    }

    /**
     * Показ BottomSheet для аутентификации
     */
    fun auth(lifecycleCoroutineScope: LifecycleCoroutineScope, activity: AppCompatActivity): Job {
        return lifecycleCoroutineScope.launch(Dispatchers.Main) {
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


    private fun checkAuthenticationFingerprint() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun initFingerPrintAuth(activity: AppCompatActivity): FingerprintAuthenticator {
        executor = ContextCompat.getMainExecutor(activity)
        biometricPrompt =
            BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        successCallBack()
                    }

                    override fun onAuthenticationError(
                        errorCode: Int, errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        setCancelCallback()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        failedCallBack()
                    }
                })

        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle(titleText)
            .setNegativeButtonText(cancelButtonText).build()

        return this
    }

}