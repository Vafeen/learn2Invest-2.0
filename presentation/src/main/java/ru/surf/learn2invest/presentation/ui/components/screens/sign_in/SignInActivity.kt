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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.cryptography.PasswordHasher
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.domain.utils.tapOn
import ru.surf.learn2invest.domain.utils.withContextIO
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivitySignInBinding
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import javax.inject.Inject

/**
 * Активити ввода PIN-кода.
 *
 * Функции:
 * - Создание PIN-кода
 * - Смена PIN-кода
 * - Аутентификация пользователя по PIN-коду
 *
 * Определение функция с помощью intent.action и [SignINActivityActions][ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions]
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
        lifecycleScope.launchMAIN {
            viewModel.dotsFlow.collect { state ->
                Log.d("dots", "collect $state")
                paintDotsDependsOnState(state)
            }
        }

        lifecycleScope.launchMAIN {
            viewModel.pinFlow.collect { pin ->
                if (pin.length == 4) {
                    viewModel.blockKeyBoard()
                    when (intent.action) {
                        SignINActivityActions.SignIN.action -> {
                            signInActions()
                        }

                        SignINActivityActions.SignUP.action -> {
                            signUpActions(pin)
                        }

                        SignINActivityActions.ChangingPIN.action -> {
                            changingPINActions(pin)
                        }
                    }
                }
            }
        }

        viewModel.fingerprintAuthenticator.setSuccessCallback {
            lifecycleScope.launchIO {
                if (intent.action == SignINActivityActions.SignUP.action) {
                    viewModel.updateProfile {
                        it.copy(biometry = true)
                    }
                }
                viewModel.onAuthenticationSucceeded(
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

    private fun changingPINActions(pin: String) {
        lifecycleScope.launchMAIN {
            when {
                // ввод старго пина
                viewModel.firstPin == "" && !viewModel.isVerified -> {
                    viewModel.isVerified = viewModel.verifyPIN()
                    viewModel.animatePINCode(
                        binding.dot1, binding.dot2, binding.dot3, binding.dot4,
                        needReturn = true, truePIN = viewModel.isVerified
                    ) {
                        viewModel.clearPIN()
                        if (viewModel.isVerified) binding.enterPin.text = getString(R.string.enter_new_pin)
                        viewModel.unblockKeyBoard()
                    }
                }

                //ввод нового
                viewModel.firstPin == "" && viewModel.isVerified -> {
                    viewModel.firstPin = pin
                    delay(500)
                    viewModel.clearPIN()
                    binding.enterPin.text = ContextCompat.getString(
                        this@SignInActivity, R.string.repeat_pin
                    )
                    viewModel.unblockKeyBoard()
                }

                // повторение нового
                viewModel.firstPin != "" && viewModel.isVerified -> {
                    val truth = pin == viewModel.firstPin
                    if (truth) {
                        viewModel.updateProfile {
                            it.copy(
                                hash = passwordHasher.passwordToHash(
                                    firstName = it.firstName,
                                    lastName = it.lastName,
                                    password = pin
                                )
                            )
                        }
                    }

                    viewModel.animatePINCode(
                        binding.dot1, binding.dot2, binding.dot3, binding.dot4,
                        needReturn = !truth, truePIN = truth
                    ) {
                        if (truth) {
                            viewModel.onAuthenticationSucceeded(
                                action = intent.action ?: "",
                                context = this@SignInActivity,
                            )
                        } else {
                            viewModel.clearPIN()
                            viewModel.unblockKeyBoard()
                        }
                    }
                }
            }
        }
    }

    private suspend fun signInActions() {
        val isAuthSucceeded = viewModel.verifyPIN()
        viewModel.animatePINCode(
            binding.dot1, binding.dot2, binding.dot3, binding.dot4,
            needReturn = !isAuthSucceeded, truePIN = isAuthSucceeded
        ) {
            if (isAuthSucceeded) {
                viewModel.onAuthenticationSucceeded(
                    action = intent.action ?: "",
                    context = this@SignInActivity,
                )
            } else {
                viewModel.clearPIN()
                viewModel.unblockKeyBoard()
            }
        }
    }


    private fun signUpActions(pin: String) {
        when {
            viewModel.firstPin == "" -> {
                viewModel.firstPin = pin
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)
                    viewModel.clearPIN()
                    binding.enterPin.text = getString(R.string.repeat_pin)
                    viewModel.unblockKeyBoard()
                }
            }

            viewModel.firstPin == pin -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    withContextIO {
                        viewModel.updateProfile {
                            it.copy(
                                hash = passwordHasher.passwordToHash(
                                    firstName = it.firstName,
                                    lastName = it.lastName,
                                    password = pin
                                )
                            )
                        }
                    }
                    viewModel.animatePINCode(
                        binding.dot1, binding.dot2, binding.dot3, binding.dot4,
                        needReturn = false, truePIN = true
                    ) {
                        val auth = {
                            viewModel.onAuthenticationSucceeded(
                                action = intent.action ?: "",
                                context = this@SignInActivity,
                            )
                        }
                        if (viewModel.fingerprintAuthenticator.isBiometricAvailable(
                                activity = this@SignInActivity
                            )
                        ) {
                            viewModel.fingerprintAuthenticator.setSuccessCallback {
                                lifecycleScope.launchMAIN {
                                    withContextIO {
                                        viewModel.updateProfile {
                                            it.copy(biometry = true)
                                        }
                                    }
                                    auth()
                                }
                            }.setCancelCallback {
                                auth()
                            }.auth(lifecycleScope, this@SignInActivity)
                        } else {
                            auth()
                        }
                    }

                }
            }

            viewModel.firstPin != pin -> {
                lifecycleScope.launchMAIN {
                    viewModel.animatePINCode(
                        binding.dot1, binding.dot2, binding.dot3, binding.dot4,
                        needReturn = true, truePIN = false
                    ) {
                        viewModel.clearPIN()
                        viewModel.unblockKeyBoard()
                    }
                }
            }
        }
    }
}