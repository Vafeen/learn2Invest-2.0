package ru.surf.learn2invest.presentation.ui.components.screens.sign_in

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.surf.learn2invest.domain.ProfileManager
import ru.surf.learn2invest.domain.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.domain.cryptography.usecase.VerifyPINUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.presentation.ui.components.screens.host.HostActivity
import javax.inject.Inject

@HiltViewModel
internal class SignInActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    var fingerprintAuthenticator: FingerprintAuthenticator,
    private val verifyPINUseCase: VerifyPINUseCase,
) :
    ViewModel() {
    var firstPin: String = ""
    var isVerified = false
    val profileFlow = profileManager.profileFlow
    private val _pinFlow = MutableStateFlow("")
    val pinFlow = _pinFlow.asStateFlow()
    private val _keyBoardIsWorkFLow = MutableStateFlow(true)
    val keyBoardIsWorkFLow = _keyBoardIsWorkFLow.asStateFlow()
    private val _dotsFlow = MutableStateFlow(
        DotsState(
            one = DotState.NULL,
            two = DotState.NULL,
            three = DotState.NULL,
            four = DotState.NULL,
        )
    )
    val dotsFlow = _dotsFlow.asStateFlow()

    fun verifyPIN(): Boolean = verifyPINUseCase.invoke(profileFlow.value, pinFlow.value)

    fun blockKeyBoard() {
        _keyBoardIsWorkFLow.update { false }
    }

    fun unblockKeyBoard() {
        _keyBoardIsWorkFLow.update { true }
    }

    fun onAuthenticationSucceeded(
        action: String,
        context: Activity
    ) {
        if (action != SignINActivityActions.ChangingPIN.action)
            context.startActivity(Intent(context, HostActivity::class.java))
        context.finish()
    }

    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    fun addSymbolToPin(symbol: String) {
        _pinFlow.update { if (it.length < 4) "$it$symbol" else it }
        paintDotsDependsOnPIN()
    }

    fun removeLastSymbolFromPIN() {
        _pinFlow.update { it.dropLast(1) }
        paintDotsDependsOnPIN()
    }

    fun clearPIN() {
        _pinFlow.update { "" }
        paintDotsDependsOnPIN()
    }

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

    private fun fULLOrNULL(length: Int) =
        if (pinFlow.value.length >= length) DotState.FULL else DotState.NULL
}