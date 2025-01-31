package ru.surf.learn2invest.presentation.ui.components.screens.sign_up

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivitySignUpBinding
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.presentation.utils.launchIO
import ru.surf.learn2invest.presentation.utils.launchMAIN
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import ru.surf.learn2invest.presentation.utils.textListener

/** Активити регистрации пользователя
 */

@AndroidEntryPoint
internal class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val viewModel: SignUpActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setNavigationBarColor(window, this, R.color.white, R.color.main_background_dark)
        setStatusBarColor(window, this, R.color.accent_background, R.color.accent_background_dark)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNameEditText()
        setupLastnameEditText()


        binding.signupBtn.setOnClickListener {
            signUpButtonClick()
        }

        binding.inputNameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                return@setOnEditorActionListener onNextClicked()
            }
            false
        }

        binding.inputLastnameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return@setOnEditorActionListener onDoneClicked(viewModel.lastNameFlow.value)
            }
            false
        }

        applyThemeColors()
        lifecycleScope.launchMAIN {
            viewModel.firstnameFlow.collect {
                validateFirstname(it)
            }
        }
        lifecycleScope.launchMAIN {
            viewModel.lastNameFlow.collect {
                validateLastname(it)
            }
        }
        lifecycleScope.launchMAIN {
            viewModel.stateFlow.collect { state ->
                binding.signupBtn.isEnabled =
                    validateFirstname(state.firstName) && validateLastname(state.lastName)
                applyThemeColors()
            }
        }
    }

    private fun setupNameEditText() {
        binding.inputNameEditText.setText(viewModel.firstnameFlow.value)
        binding.inputNameEditText.addTextChangedListener(textListener(onTextChanged = { s, _, _, _ ->
            lifecycleScope.launchIO {
                viewModel.updateFirstname(s.toString())
            }
        }))
        with(binding.inputNameEditText) {
            requestFocus()
            showKeyboard()
        }
    }

    private fun setupLastnameEditText() {
        binding.inputLastnameEditText.setText(viewModel.lastNameFlow.value)
        binding.inputLastnameEditText.addTextChangedListener(
            textListener(onTextChanged = { s, _, _, _ ->
                lifecycleScope.launchIO {
                    viewModel.updateLastName(s.toString())
                }
            })
        )
    }


    private fun applyThemeColors() {
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.signupBtn.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(
                if (binding.signupBtn.isEnabled) {
                    if (isDarkTheme)
                        R.color.accent_background_dark
                    else
                        R.color.accent_background
                } else {
                    if (isDarkTheme)
                        R.color.accent_button_dark
                    else
                        R.color.btn_asset
                }
            )
        )
    }

    private fun validateFirstname(firstname: String): Boolean = when {
        firstname.isEmpty() -> {
            false
        }

        firstname.trim() != firstname -> {
            binding.nameErrorTextView.text =
                ContextCompat.getString(this, R.string.contains_spaces)
            binding.nameErrorTextView.isVisible = true
            false
        }

        firstname.length > viewModel.lengthLimit -> {
            binding.nameErrorTextView.text =
                ContextCompat.getString(this, R.string.limit_len_exceeded)
            binding.nameErrorTextView.isVisible = true
            false
        }

        else -> {
            binding.nameErrorTextView.isVisible = false
            true
        }
    }


    private fun validateLastname(lastName: String): Boolean = when {
        lastName.isEmpty() -> {
            false
        }

        lastName.trim() != lastName -> {
            binding.lastnameErrorTextView.text =
                ContextCompat.getString(this, R.string.contains_spaces)
            binding.lastnameErrorTextView.isVisible = true
            false
        }

        lastName.length > viewModel.lengthLimit -> {
            binding.lastnameErrorTextView.text =
                ContextCompat.getString(this, R.string.limit_len_exceeded)
            binding.lastnameErrorTextView.isVisible = true
            false
        }

        else -> {
            binding.lastnameErrorTextView.isVisible = false
            true
        }
    }


    private fun onNextClicked(): Boolean {
        if (viewModel.firstnameFlow.value.isEmpty()) {
            binding.nameErrorTextView.text = ContextCompat.getString(this, R.string.empty_error)
            binding.nameErrorTextView.isVisible = true
            return true
        }
        binding.inputLastnameEditText.requestFocus()
        return false
    }

    private fun onDoneClicked(lastName: String): Boolean {
        if (lastName.isEmpty()) {
            binding.lastnameErrorTextView.text = ContextCompat.getString(this, R.string.empty_error)
            binding.lastnameErrorTextView.isVisible = true
            return true
        } else {
            binding.inputLastnameEditText.hideKeyboard()
            binding.inputLastnameEditText.clearFocus()
        }
        return false
    }

    private fun signUpButtonClick() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.updateProfile {
                it.copy(
                    firstName = viewModel.firstnameFlow.value,
                    lastName = viewModel.lastNameFlow.value
                )
            }
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java).apply {
                action = SignINActivityActions.SignUP.action
            })
            this@SignUpActivity.finish()
        }
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.show(WindowInsetsCompat.Type.ime())
}