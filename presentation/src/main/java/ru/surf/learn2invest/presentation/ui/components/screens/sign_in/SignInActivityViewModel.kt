package ru.surf.learn2invest.presentation.ui.components.screens.sign_in

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.ProfileManager
import ru.surf.learn2invest.domain.cryptography.FingerprintAuthenticator
import ru.surf.learn2invest.domain.cryptography.usecase.VerifyPINUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.presentation.ui.components.screens.host.HostActivity
import javax.inject.Inject

@HiltViewModel
class SignInActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    var fingerprintAuthenticator: FingerprintAuthenticator,
    private val verifyPINUseCase: VerifyPINUseCase,
) :
    ViewModel() {
    var pinCode: String = ""
    var firstPin: String = ""
    var isVerified = false
    var userDataIsChanged = false
    var keyBoardIsWork = true
    val profileFlow = profileManager.profileFlow
    fun verifyPIN(): Boolean = verifyPINUseCase.invoke(profileFlow.value, pinCode)

    fun blockKeyBoard() {
        keyBoardIsWork = false
    }

    fun unBlockKeyBoard() {
        keyBoardIsWork = true
    }

    fun onAuthenticationSucceeded(
        action: String,
        context: Activity
    ) {
        if (action != SignINActivityActions.ChangingPIN.action)
            context.startActivity(Intent(context, HostActivity::class.java))
        pinCode = ""
        context.finish()
    }

    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }
}