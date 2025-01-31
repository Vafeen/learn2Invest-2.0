package ru.surf.learn2invest.presentation.ui.components.screens.sign_in

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import ru.surf.learn2invest.domain.cryptography.PasswordHasher
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivitySignInBinding
import ru.surf.learn2invest.presentation.utils.gotoCenter
import ru.surf.learn2invest.presentation.utils.launchIO
import ru.surf.learn2invest.presentation.utils.launchMAIN
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import ru.surf.learn2invest.presentation.utils.tapOn
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
internal class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInActivityViewModel by viewModels()
    private lateinit var dots: DotsState<Drawable>

    @Inject
    lateinit var passwordHasher: PasswordHasher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor(window, this, R.color.accent_background, R.color.accent_background_dark)
        setNavigationBarColor(
            window, this, R.color.accent_background, R.color.accent_background_dark
        )

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dots = DotsState(
            one = binding.dot1.drawable,
            two = binding.dot2.drawable,
            three = binding.dot3.drawable,
            four = binding.dot4.drawable
        )
        initListeners()

        when (intent.action) {
            SignINActivityActions.SignIN.action -> {
                if (viewModel.profileFlow.value.biometry) {
                    viewModel.fingerprintAuthenticator.auth(
                        coroutineScope = lifecycleScope, activity = this@SignInActivity
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
        viewModel.blockKeyBoard()
        delay(100)
        binding.apply {
            dot1.gotoCenter(truePIN = truth,
                needReturn = needReturn,
                lifecycleScope = lifecycleScope,
                doAfter = { viewModel.unblockKeyBoard() })
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


    private fun paintDotsDependsOnState(dotsState: DotsState<DotState>) {
        paintDotDependsOnState(dots.one, dotsState.one)
        paintDotDependsOnState(dots.two, dotsState.two)
        paintDotDependsOnState(dots.three, dotsState.three)
        paintDotDependsOnState(dots.four, dotsState.four)
    }

    private fun paintDotDependsOnState(drawable: Drawable, state: DotState) {
        drawable.setTint(
            when (state) {
                DotState.NULL -> Color.WHITE
                DotState.FULL -> Color.BLACK
                DotState.RIGHT -> Color.GREEN
                DotState.ERROR -> Color.RED
            }
        )
    }


    private fun initListeners() {
        viewModel.apply {
            lifecycleScope.launchMAIN {
                viewModel.dotsFlow.collect { state ->
                    Log.d("dots", "collect $state")
                    paintDotsDependsOnState(state)
                }
            }

            lifecycleScope.launchMAIN {
                viewModel.pinFlow.collect {
                    if (it.length == 4) {
                        when (intent.action) {
                            SignINActivityActions.SignIN.action -> {
                                lifecycleScope.launchMAIN {
                                    val isAuthSucceeded = verifyPIN()
                                    animatePINCode(isAuthSucceeded)
                                    if (isAuthSucceeded) onAuthenticationSucceeded(
                                        action = intent.action ?: "",
                                        context = this@SignInActivity,
                                    )
                                    else viewModel.clearPIN()
                                }
                            }

//                        SignINActivityActions.SignUP.action -> {
//                            when {
//                                firstPin == "" -> {
//                                    firstPin = pinCode
//                                    pinCode = ""
//                                    lifecycleScope.launch(Dispatchers.Main) {
//                                        delay(500)
//                                        binding.enterPin.text = getString(R.string.repeat_pin)
//                                        unBlockKeyBoard()
//                                    }
//                                }
//
//                                firstPin == pinCode -> {
//                                    lifecycleScope.launch(Dispatchers.Main) {
//                                        viewModel.updateProfile {
//                                            it.copy(
//                                                hash = passwordHasher.passwordToHash(
//                                                    firstName = it.firstName,
//                                                    lastName = it.lastName,
//                                                    password = pinCode
//                                                )
//                                            )
//                                        }
//                                        animatePINCode(truth = true)
//                                        if (viewModel.fingerprintAuthenticator.isBiometricAvailable(
//                                                activity = this@SignInActivity
//                                            )
//                                        ) {
//                                            viewModel.fingerprintAuthenticator.setSuccessCallback {
//                                                lifecycleScope.launch {
//                                                    viewModel.updateProfile {
//                                                        it.copy(biometry = true)
//                                                    }
//                                                    onAuthenticationSucceeded(
//                                                        action = intent.action ?: "",
//                                                        context = this@SignInActivity,
//                                                    )
//                                                }
//                                            }.setCancelCallback {
//                                                onAuthenticationSucceeded(
//                                                    action = intent.action ?: "",
//                                                    context = this@SignInActivity,
//                                                )
//                                            }.auth(lifecycleScope, this@SignInActivity)
//                                        } else {
//                                            onAuthenticationSucceeded(
//                                                action = intent.action ?: "",
//                                                context = this@SignInActivity,
//                                            )
//                                        }
//                                    }
//                                }
//
//                                firstPin != pinCode -> {
//                                    lifecycleScope.launch(Dispatchers.Main) {
//                                        pinCode = ""
//                                        animatePINCode(truth = false)
//                                    }
//                                }
//                            }
//
//                        }
//
//                        SignINActivityActions.ChangingPIN.action -> {
//                            when {
//                                // вводит старый пароль
//                                firstPin == "" && !isVerified -> {
//                                    lifecycleScope.launch(Dispatchers.Main) {
//                                        //если ввел верно
//                                        isVerified = verifyPIN()
//                                        pinCode = ""
//                                        animatePINCode(
//                                            truth = isVerified, needReturn = true
//                                        )
//                                        if (isVerified) binding.enterPin.text =
//                                            ContextCompat.getString(
//                                                this@SignInActivity, R.string.enter_new_pin
//                                            )
//                                        unBlockKeyBoard()
//                                    }
//                                }
//
//                                //вводит новый
//                                firstPin == "" && isVerified -> {
//                                    lifecycleScope.launch(Dispatchers.Main) {
//                                        firstPin = pinCode
//                                        pinCode = ""
//                                        delay(500)
//                                        binding.enterPin.text = ContextCompat.getString(
//                                            this@SignInActivity, R.string.repeat_pin
//                                        )
//                                        unBlockKeyBoard()
//                                    }
//
//                                }
//
//                                // повторяет
//                                firstPin != "" && isVerified -> {
//                                    lifecycleScope.launch(Dispatchers.Main) {
//                                        val truth = pinCode == firstPin
//                                        if (truth) {
//                                            viewModel.updateProfile {
//                                                it.copy(
//                                                    hash = passwordHasher.passwordToHash(
//                                                        firstName = it.firstName,
//                                                        lastName = it.lastName,
//                                                        password = viewModel.pinCode
//                                                    )
//                                                )
//                                            }
//
//                                        }
//
//                                        animatePINCode(
//                                            truth = truth, needReturn = true
//                                        )
//                                        pinCode = ""
//                                        if (truth) onAuthenticationSucceeded(
//                                            action = intent.action ?: "",
//                                            context = this@SignInActivity,
//                                        )
//                                    }
//                                }
//                            }
//                        }
                        }
                    }
                }
            }

            fingerprintAuthenticator.setSuccessCallback {
                lifecycleScope.launchIO {
                    if (intent.action == SignINActivityActions.SignUP.action) {
                        viewModel.updateProfile {
                            it.copy(biometry = true)
                        }
                    }

                    onAuthenticationSucceeded(
                        action = intent.action ?: "",
                        context = this@SignInActivity,
                    )
                }
            }.setDesignBottomSheet(
                title = ContextCompat.getString(
                    this@SignInActivity, R.string.sign_in_in_learn2invest
                ), cancelText = ContextCompat.getString(this@SignInActivity, R.string.cancel)
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
                        viewModel.addSymbolToPin("$index")
                        it.tapOn()
                    }
                }

                backspace.setOnClickListener {
                    viewModel.removeLastSymbolFromPIN()
                }

                lifecycleScope.launchMAIN {
                    viewModel.keyBoardIsWorkFLow.collect { isEnabled ->
                        (numberButtons + backspace).forEach { button ->
                            button.isEnabled = isEnabled
                        }
                    }
                }
                fingerprint.isVisible =
                    if (viewModel.fingerprintAuthenticator.isBiometricAvailable(activity = this@SignInActivity) && viewModel.profileFlow.value.biometry) {
                        fingerprint.setOnClickListener {
                            viewModel.fingerprintAuthenticator.auth(
                                lifecycleScope, this@SignInActivity
                            )
                        }
                        true
                    } else false
            }
        }
    }
}