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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivitySignUpBinding
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import ru.surf.learn2invest.presentation.utils.textListener
/**
 * Activity для регистрации нового пользователя. Обрабатывает ввод имени и фамилии пользователя,
 * их валидацию и обновление данных профиля. После успешной регистрации, пользователь будет
 * перенаправлен на экран входа.
 */
@AndroidEntryPoint
internal class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val viewModel: SignUpActivityViewModel by viewModels()

    /**
     * Метод, который вызывается при создании активности. Настроены поля ввода имени и фамилии,
     * а также обработчики событий для кнопки регистрации и действий с клавиатурой.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Настройка цветов навигационной панели и статус-бара
        setNavigationBarColor(window, this, R.color.white, R.color.main_background_dark)
        setStatusBarColor(window, this, R.color.accent_background, R.color.accent_background_dark)

        // Инициализация привязки и отображение UI
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNameEditText()
        setupLastnameEditText()

        // Обработчик нажатия кнопки "Зарегистрироваться"
        binding.signupBtn.setOnClickListener {
            signUpButtonClick()
        }

        // Обработчики действий для переходов между полями ввода
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

        // Подписки на изменения состояния ViewModel
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

    /**
     * Метод для настройки поля ввода имени пользователя.
     */
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

    /**
     * Метод для настройки поля ввода фамилии пользователя.
     */
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

    /**
     * Метод для применения цветовой схемы в зависимости от текущей темы.
     */
    private fun applyThemeColors() {
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        binding.signupBtn.backgroundTintList = ColorStateList.valueOf(
            getColor(
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

    /**
     * Валидация имени пользователя.
     * @param firstname имя пользователя
     * @return результат валидации (true если имя корректно, false иначе)
     */
    private fun validateFirstname(firstname: String): Boolean = when {
        firstname.isEmpty() -> {
            false
        }

        firstname.trim() != firstname -> {
            binding.nameErrorTextView.text = getString(R.string.contains_spaces)
            binding.nameErrorTextView.isVisible = true
            false
        }

        firstname.length > viewModel.lengthLimit -> {
            binding.nameErrorTextView.text = getString(R.string.limit_len_exceeded)
            binding.nameErrorTextView.isVisible = true
            false
        }

        else -> {
            binding.nameErrorTextView.isVisible = false
            true
        }
    }

    /**
     * Валидация фамилии пользователя.
     * @param lastName фамилия пользователя
     * @return результат валидации (true если фамилия корректна, false иначе)
     */
    private fun validateLastname(lastName: String): Boolean = when {
        lastName.isEmpty() -> {
            false
        }

        lastName.trim() != lastName -> {
            binding.lastnameErrorTextView.text = getString(R.string.contains_spaces)
            binding.lastnameErrorTextView.isVisible = true
            false
        }

        lastName.length > viewModel.lengthLimit -> {
            binding.lastnameErrorTextView.text = getString(R.string.limit_len_exceeded)
            binding.lastnameErrorTextView.isVisible = true
            false
        }

        else -> {
            binding.lastnameErrorTextView.isVisible = false
            true
        }
    }

    /**
     * Обработчик нажатия кнопки "Следующий" в поле имени.
     * @return true если имя пустое, иначе false
     */
    private fun onNextClicked(): Boolean {
        if (viewModel.firstnameFlow.value.isEmpty()) {
            binding.nameErrorTextView.text = getString(R.string.empty_error)
            binding.nameErrorTextView.isVisible = true
            return true
        }
        binding.inputLastnameEditText.requestFocus()
        return false
    }

    /**
     * Обработчик нажатия кнопки "Готово" в поле фамилии.
     * @param lastName фамилия пользователя
     * @return true если фамилия пустая, иначе false
     */
    private fun onDoneClicked(lastName: String): Boolean {
        if (lastName.isEmpty()) {
            binding.lastnameErrorTextView.text = getString(R.string.empty_error)
            binding.lastnameErrorTextView.isVisible = true
            return true
        } else {
            binding.inputLastnameEditText.hideKeyboard()
            binding.inputLastnameEditText.clearFocus()
        }
        return false
    }

    /**
     * Обработчик нажатия кнопки "Зарегистрироваться".
     * Обновляет профиль пользователя и переходит на экран входа.
     */
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

    /**
     * Скрытие клавиатуры для текущего элемента.
     */
    private fun View.hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * Показ клавиатуры для текущего элемента.
     */
    private fun View.showKeyboard() = WindowCompat.getInsetsController(window,this).show(WindowInsetsCompat.Type.ime())
}

