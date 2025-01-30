package ru.surf.learn2invest.presentation.ui.components.screens.sign_in

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.cryptography.PasswordHasher
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivitySignInBinding
import ru.surf.learn2invest.presentation.utils.gotoCenter
import ru.surf.learn2invest.presentation.utils.launchIO
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import ru.surf.learn2invest.presentation.utils.tapOn
import ru.surf.learn2invest.presentation.utils.withContextMAIN
import javax.inject.Inject

/**
 * Активити ввода PIN-кода.
 *
 * Функции:
 * - Создание PIN-кода
 * - Смена PIN-кода
 * - Аутентификация пользователя по PIN-коду
 *
 * Определение функция с помощью intent.action и [SignINActivityActions][ru.surf.learn2invest.ui.components.screens.sign_in.SignINActivityActions]
 */

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInActivityViewModel by viewModels()

    @Inject
    lateinit var passwordHasher: PasswordHasher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor(window, this, R.color.accent_background, R.color.accent_background_dark)
        setNavigationBarColor(
            window,
            this,
            R.color.accent_background,
            R.color.accent_background_dark
        )

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
        paintDots()
        when (intent.action) {
            SignINActivityActions.SignIN.action -> {
                if (viewModel.profileFlow.value.biometry) {
                    viewModel.fingerprintAuthenticator.auth(
                        lifecycleCoroutineScope = lifecycleScope,
                        activity = this@SignInActivity
                    )
                }
            }

            SignINActivityActions.SignUP.action -> {
                binding.enterPin.text =
                    ContextCompat.getString(this@SignInActivity, R.string.create_pin)

                binding.fingerprint.isVisible = false
            }

            SignINActivityActions.ChangingPIN.action -> {
                binding.enterPin.text =
                    ContextCompat.getString(this@SignInActivity, R.string.enter_old_pin)

                binding.fingerprint.isVisible = false
            }
        }
    }

    private suspend fun animatePINCode(truth: Boolean, needReturn: Boolean = false) {
        delay(100)
        binding.apply {
            dot1.gotoCenter(
                truePIN = truth,
                needReturn = needReturn,
                lifecycleScope = lifecycleScope,
                doAfter = { viewModel.unBlockKeyBoard() }
            )
            dot2.gotoCenter(
                truePIN = truth, needReturn = needReturn, lifecycleScope = lifecycleScope
            )
            dot3.gotoCenter(
                truePIN = truth, needReturn = needReturn, lifecycleScope = lifecycleScope
            )
            dot4.gotoCenter(
                truePIN = truth, needReturn = needReturn, lifecycleScope = lifecycleScope
            )
        }
        delay(800)
    }

    private fun changeColorOfFourDots(
        color1: Int,
        color2: Int,
        color3: Int,
        color4: Int,
    ) {
        binding.dot1.drawable.setTint(color1)
        binding.dot2.drawable.setTint(color2)
        binding.dot3.drawable.setTint(color3)
        binding.dot4.drawable.setTint(color4)
    }

    private fun paintDots(count: Int = viewModel.pinCode.length) {
        when (count) {
            1 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.WHITE,
                    color3 = Color.WHITE,
                    color4 = Color.WHITE,
                )
            }

            2 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.BLACK,
                    color3 = Color.WHITE,
                    color4 = Color.WHITE,
                )
            }

            3 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.BLACK,
                    color3 = Color.BLACK,
                    color4 = Color.WHITE,
                )
            }

            4 -> {
                changeColorOfFourDots(
                    color1 = Color.BLACK,
                    color2 = Color.BLACK,
                    color3 = Color.BLACK,
                    color4 = Color.BLACK,
                )

            }

            // error
            -1 -> {
                changeColorOfFourDots(
                    color1 = Color.RED,
                    color2 = Color.RED,
                    color3 = Color.RED,
                    color4 = Color.RED,
                )
            }

            else -> {
                changeColorOfFourDots(
                    color1 = Color.WHITE,
                    color2 = Color.WHITE,
                    color3 = Color.WHITE,
                    color4 = Color.WHITE
                )
            }
        }
    }

    private fun updatePin(num: String) {
        viewModel.apply {
            if (keyBoardIsWork) {
                if (pinCode.length < 4) pinCode += num
                paintDots()
                if (pinCode.length == 4) {
                    blockKeyBoard()
                    when (intent.action) {

                        SignINActivityActions.SignIN.action -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                val isAuthSucceeded = verifyPIN()
                                animatePINCode(isAuthSucceeded)
                                if (isAuthSucceeded) onAuthenticationSucceeded(
                                    action = intent.action ?: "",
                                    context = this@SignInActivity,
                                )
                                else pinCode = ""
                            }
                        }

                        SignINActivityActions.SignUP.action -> {
                            when {
                                firstPin == "" -> {
                                    firstPin = pinCode
                                    pinCode = ""
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        delay(500)
                                        paintDots()
                                        binding.enterPin.text = getString(R.string.repeat_pin)
                                        unBlockKeyBoard()
                                    }
                                }

                                firstPin == pinCode -> {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        viewModel.updateProfile {
                                            it.copy(
                                                hash = passwordHasher.passwordToHash(
                                                    firstName = it.firstName,
                                                    lastName = it.lastName,
                                                    password = pinCode
                                                )
                                            )
                                        }
                                        animatePINCode(truth = true)
                                        if (viewModel.fingerprintAuthenticator.isBiometricAvailable(
                                                activity = this@SignInActivity
                                            )
                                        ) {
                                            viewModel.fingerprintAuthenticator.setSuccessCallback {
                                                lifecycleScope.launch {
                                                    viewModel.updateProfile {
                                                        it.copy(biometry = true)
                                                    }
                                                    onAuthenticationSucceeded(
                                                        action = intent.action ?: "",
                                                        context = this@SignInActivity,
                                                    )
                                                }
                                            }.setCancelCallback {
                                                onAuthenticationSucceeded(
                                                    action = intent.action ?: "",
                                                    context = this@SignInActivity,
                                                )
                                            }.auth(lifecycleScope, this@SignInActivity)
                                        } else {
                                            onAuthenticationSucceeded(
                                                action = intent.action ?: "",
                                                context = this@SignInActivity,
                                            )
                                        }
                                    }
                                }

                                firstPin != pinCode -> {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        pinCode = ""
                                        animatePINCode(truth = false)
                                    }
                                }
                            }

                        }

                        SignINActivityActions.ChangingPIN.action -> {
                            when {
                                // вводит старый пароль
                                firstPin == "" && !isVerified -> {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        //если ввел верно
                                        isVerified = verifyPIN()
                                        pinCode = ""
                                        animatePINCode(
                                            truth = isVerified, needReturn = true
                                        )
                                        if (isVerified) binding.enterPin.text =
                                            ContextCompat.getString(
                                                this@SignInActivity,
                                                R.string.enter_new_pin
                                            )
                                        paintDots()
                                        unBlockKeyBoard()
                                    }
                                }

                                //вводит новый
                                firstPin == "" && isVerified -> {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        firstPin = pinCode
                                        pinCode = ""
                                        delay(500)
                                        paintDots()
                                        binding.enterPin.text =
                                            ContextCompat.getString(
                                                this@SignInActivity,
                                                R.string.repeat_pin
                                            )
                                        unBlockKeyBoard()
                                    }

                                }

                                // повторяет
                                firstPin != "" && isVerified -> {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        val truth = pinCode == firstPin
                                        if (truth) {
                                            viewModel.userDataIsChanged = true
                                            viewModel.updateProfile {
                                                it.copy(
                                                    hash = passwordHasher.passwordToHash(
                                                        firstName = it.firstName,
                                                        lastName = it.lastName,
                                                        password = viewModel.pinCode
                                                    )
                                                )
                                            }

                                        }

                                        animatePINCode(
                                            truth = truth, needReturn = true
                                        )
                                        pinCode = ""
                                        if (truth) onAuthenticationSucceeded(
                                            action = intent.action ?: "",
                                            context = this@SignInActivity,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        viewModel.apply {
            fingerprintAuthenticator.setSuccessCallback {
                lifecycleScope.launchIO {
                    if (intent.action == SignINActivityActions.SignUP.action) {

                        viewModel.updateProfile {
                            it.copy(biometry = true)
                        }
                        userDataIsChanged = true

                    }
                    withContextMAIN {
                        animatePINCode(truth = true)
                    }
                    onAuthenticationSucceeded(
                        action = intent.action ?: "",
                        context = this@SignInActivity,
                    )
                }
            }.setDesignBottomSheet(
                title = ContextCompat.getString(
                    this@SignInActivity,
                    R.string.sign_in_in_learn2invest
                ),
                cancelText = ContextCompat.getString(this@SignInActivity, R.string.cancel)
            )

            binding.apply {
                val numberButtons = listOf(
                    button0,
                    button1,
                    button2,
                    button3,
                    button4,
                    button5,
                    button6,
                    button7,
                    button8,
                    button9,
                )

                for (index in 0..numberButtons.lastIndex) {
                    numberButtons[index].setOnClickListener {
                        updatePin("$index")
                        (it as TextView).tapOn()
                    }
                }

                backspace.setOnClickListener {
                    if (pinCode.isNotEmpty()) {
                        pinCode =
                            pinCode.substring(0, pinCode.lastIndex)
                        paintDots(count = pinCode.length)
                    }
                }

                fingerprint.isVisible =
                    if (viewModel.fingerprintAuthenticator.isBiometricAvailable(activity = this@SignInActivity) && viewModel.profileFlow.value.biometry) {
                        fingerprint.setOnClickListener {
                            viewModel.fingerprintAuthenticator.auth(
                                lifecycleScope,
                                this@SignInActivity
                            )
                        }
                        true
                    } else false
            }
        }
    }
}