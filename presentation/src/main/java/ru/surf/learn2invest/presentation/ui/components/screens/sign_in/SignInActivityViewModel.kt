package ru.surf.learn2invest.presentation.ui.components.screens.sign_in

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.surf.learn2invest.domain.animator.usecase.AnimateDotsUseCase
import ru.surf.learn2invest.domain.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.domain.cryptography.usecase.VerifyPINUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.presentation.ui.components.screens.host.HostActivity
import javax.inject.Inject
/**
 * ViewModel для экрана входа (SignInActivity), обрабатывающий аутентификацию с использованием PIN-кода и отпечатка пальца.
 * Обрабатывает логику аутентификации, блокировку/разблокировку клавиатуры и анимацию точек в процессе ввода PIN-кода.
 *
 * @param profileManager Менеджер профиля для получения и обновления данных профиля.
 * @param fingerprintAuthenticator Аутентификатор отпечатка пальца для работы с биометрической аутентификацией.
 * @param verifyPINUseCase Кейс для верификации PIN-кода.
 * @param animateDotsUseCase Кейс для анимации точек PIN-кода.
 */
@HiltViewModel
internal class SignInActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    var fingerprintAuthenticator: FingerprintAuthenticator,
    private val verifyPINUseCase: VerifyPINUseCase,
    private val animateDotsUseCase: AnimateDotsUseCase,
) : ViewModel() {

    /**
     * Первый PIN-код, введенный пользователем.
     */
    var firstPin: String = ""

    /**
     * Флаг, указывающий на успешную верификацию.
     */
    var isVerified = false

    /**
     * Поток профиля, предоставляющий доступ к данным текущего профиля.
     */
    val profileFlow = profileManager.profileFlow

    /**
     * Поток, представляющий текущий PIN-код.
     */
    private val _pinFlow = MutableStateFlow("")
    val pinFlow = _pinFlow.asStateFlow()

    /**
     * Поток, который управляет состоянием клавиатуры (активна или заблокирована).
     */
    private val _keyBoardIsWorkFLow = MutableStateFlow(true)
    val keyBoardIsWorkFLow = _keyBoardIsWorkFLow.asStateFlow()

    /**
     * Поток, содержащий состояние точек PIN-кода (заполненные или пустые).
     */
    private val _dotsFlow = MutableStateFlow(
        DotsState(
            one = DotState.NULL,
            two = DotState.NULL,
            three = DotState.NULL,
            four = DotState.NULL,
        )
    )
    val dotsFlow = _dotsFlow.asStateFlow()

    /**
     * Проверка правильности введенного PIN-кода.
     *
     * @return true, если PIN-код верный, иначе false.
     */
    fun verifyPIN(): Boolean = verifyPINUseCase.invoke(profileFlow.value, pinFlow.value)

    /**
     * Блокирует клавиатуру, предотвращая дальнейший ввод.
     */
    fun blockKeyBoard() {
        _keyBoardIsWorkFLow.update { false }
    }

    /**
     * Разблокирует клавиатуру, позволяя вводить символы.
     */
    fun unblockKeyBoard() {
        _keyBoardIsWorkFLow.update { true }
    }

    /**
     * Обработчик успешной аутентификации, который перенаправляет пользователя на главный экран.
     *
     * @param action Действие, определяющее, какую активность запускать.
     * @param context Контекст активности для выполнения перенаправления.
     */
    fun onAuthenticationSucceeded(
        action: String,
        context: Activity,
    ) {
        if (action != SignINActivityActions.ChangingPIN.action)
            context.startActivity(Intent(context, HostActivity::class.java))
        context.finish()
    }

    /**
     * Обновление профиля с помощью предоставленной функции для изменения данных.
     *
     * @param updating Функция для обновления профиля.
     */
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    /**
     * Добавление символа в PIN-код, обновление точек в зависимости от длины PIN-кода.
     *
     * @param symbol Символ для добавления в PIN-код.
     */
    fun addSymbolToPin(symbol: String) {
        _pinFlow.update { if (it.length < 4) "$it$symbol" else it }
        paintDotsDependsOnPIN()
    }

    /**
     * Анимация ввода PIN-кода с использованием переданных ImageView.
     *
     * @param dot1 Первая точка PIN-кода.
     * @param dot2 Вторая точка PIN-кода.
     * @param dot3 Третья точка PIN-кода.
     * @param dot4 Четвертая точка PIN-кода.
     * @param needReturn Флаг, указывающий, нужно ли возвращать анимацию в исходное состояние.
     * @param truePIN Флаг, указывающий, является ли PIN-код правильным.
     * @param onEnd Функция, которая будет вызвана по завершении анимации.
     */
    suspend fun animatePINCode(
        dot1: ImageView,
        dot2: ImageView,
        dot3: ImageView,
        dot4: ImageView,
        needReturn: Boolean,
        truePIN: Boolean,
        onEnd: () -> Unit,
    ) {
        blockKeyBoard()
        delay(100)
        animateDotsUseCase.invoke(dot1, dot2, dot3, dot4, needReturn, truePIN, onEnd)
    }

    /**
     * Удаляет последний символ из PIN-кода и обновляет точки.
     */
    fun removeLastSymbolFromPIN() {
        _pinFlow.update { it.dropLast(1) }
        paintDotsDependsOnPIN()
    }

    /**
     * Очищает весь PIN-код и обновляет точки.
     */
    fun clearPIN() {
        _pinFlow.update { "" }
        paintDotsDependsOnPIN()
    }

    /**
     * Обновляет состояние точек PIN-кода в зависимости от текущего ввода PIN.
     */
    private fun paintDotsDependsOnPIN() {
        _dotsFlow.update {
            it.copy(
                one = fULLOrNULL(1),
                two = fULLOrNULL(2),
                three = fULLOrNULL(3),
                four = fULLOrNULL(4)
            )
        }
    }

    /**
     * Возвращает состояние точки в зависимости от длины введенного PIN-кода.
     *
     * @param length Длина PIN-кода для проверки.
     * @return Состояние точки (FULL или NULL).
     */
    private fun fULLOrNULL(length: Int) =
        if (pinFlow.value.length >= length) DotState.FULL else DotState.NULL
}
