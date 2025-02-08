package ru.surf.learn2invest.presentation.ui.components.screens.trading_password

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.utils.hideKeyboard
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.domain.utils.showKeyboard
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivityTradingPasswordBinding
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import ru.surf.learn2invest.presentation.utils.textListener

/**
 * Активити торгового пароля для подтверждения сделок.
 *
 * Функции:
 * - Создание торгового пароля.
 * - Смена торгового пароля.
 * - Удаление торгового пароля.
 *
 * Определение функции с помощью intent.action и
 * [TradingPasswordActivityActions][ru.surf.learn2invest.presentation.ui.components.screens.trading_password.TradingPasswordActivityActions].
 */
@AndroidEntryPoint
internal class TradingPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTradingPasswordBinding
    private val viewModel: TradingPasswordActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initAction(intentAction = intent.action.toString())
        setStatusBarColor(window, this, R.color.white, R.color.main_background_dark)
        setNavigationBarColor(window, this, R.color.white, R.color.main_background_dark)
        binding = ActivityTradingPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configureRules()
        initListeners()
    }

    /**
     * Настраивает правила отображения для ввода пароля.
     */
    private fun configureRules() {
        binding.apply {
            rules1.text = getString(R.string.min_len_trading_password)
            rules2.text = getString(R.string.not_more_than_2)
            rules3.text = getString(R.string.no_seq_more_than_2)
            rules4.text = getString(R.string.pass_match)
            rules5.text = getString(R.string.old_pas_correct)
        }
    }

    /**
     * Настраивает изображение правила в зависимости от его состояния.
     *
     * @param isOk Указывает, является ли правило корректным (true) или нет (false).
     */
    private fun ImageView.configureRule(isOk: Boolean) = setImageDrawable(
        if (isOk) viewModel.getDrawableRes(R.drawable.ok_24dp) else viewModel.getDrawableRes(R.drawable.error_24dp)
    )

    /**
     * Инициализирует слушатели событий для элементов интерфейса.
     */
    private fun initListeners() {
        binding.apply {
            textInputLayout1.isVisible = true
            textInputLayout2.isVisible = true
            textInputLayout3.isVisible = true

            // Настройка видимости правил и изображений правил
            rules1.isVisible = true
            imageRule1.isVisible = true
            rules2.isVisible = true
            imageRule2.isVisible = true
            rules3.isVisible = true
            imageRule3.isVisible = true
            rules4.isVisible = true
            imageRule4.isVisible = true
            rules5.isVisible = true
            imageRule5.isVisible = true

            // Слушатель для действий в соответствии с потоком действий ViewModel
            lifecycleScope.launchMAIN {
                viewModel.actionFlow.collect { action ->
                    action?.let {
                        binding.header.text = getString(it.resName)
                        when (it) {
                            TradingPasswordActivityActions.CreateTradingPassword -> {
                                // Логика создания торгового пароля
                                textInputLayout1.isVisible = false
                                rules5.isVisible = false
                                imageRule5.isVisible = false

                                passwordEdit.apply {
                                    requestFocus()
                                    showKeyboard()
                                }
                            }

                            TradingPasswordActivityActions.ChangeTradingPassword -> {
                                // Логика изменения торгового пароля
                                textInputLayout1.isVisible = true
                                rules5.isVisible = true
                                imageRule5.isVisible = true

                                passwordLast.apply {
                                    requestFocus()
                                    showKeyboard()
                                }
                            }

                            TradingPasswordActivityActions.RemoveTradingPassword -> {
                                // Логика удаления торгового пароля
                                textInputLayout1.isVisible = false

                                // Скрытие всех правил и изображений правил при удалении пароля
                                rules1.isVisible = false
                                imageRule1.isVisible = false
                                rules2.isVisible = false
                                imageRule2.isVisible = false
                                rules3.isVisible = false
                                imageRule3.isVisible = false

                                rules5.isVisible = false
                                imageRule5.isVisible = false

                                passwordEdit.apply {
                                    requestFocus()
                                    showKeyboard()
                                }
                            }
                        }
                    }
                }
            }

            // Слушатель для основного действия кнопки
            lifecycleScope.launchMAIN {
                viewModel.mainButtonActionFlow.collect { buttonAction ->
                    binding.buttonDoTrading.apply {
                        text = buttonAction.text // Установка текста кнопки
                        isVisible = buttonAction.isVisible // Установка видимости кнопки
                    }
                }
            }

            // Слушатель для изменения состояния правил
            lifecycleScope.launchMAIN {
                viewModel.ruleStateFlow.collect { state ->
                    state[RuleStateKey.RULE_1]?.let { binding.imageRule1.configureRule(it) }
                    state[RuleStateKey.RULE_2]?.let { binding.imageRule2.configureRule(it) }
                    state[RuleStateKey.RULE_3]?.let { binding.imageRule3.configureRule(it) }
                    state[RuleStateKey.RULE_4]?.let { binding.imageRule4.configureRule(it) }
                    state[RuleStateKey.RULE_5]?.let { binding.imageRule5.configureRule(it) }
                }
            }

            // Обработчик нажатия на кнопку "Назад"
            arrowBack.setOnClickListener {
                this@TradingPasswordActivity.finish()
            }

            // Слушатели текстовых изменений для полей ввода паролей
            passwordLast.addTextChangedListener(textListener(onTextChanged = { s, _, _, _ ->
                s?.let { viewModel.updateLastPassword(it.toString()) }
            }))

            passwordEdit.addTextChangedListener(textListener(onTextChanged = { s, _, _, _ ->
                s?.let { viewModel.updatePassword(it.toString()) }
            }))

            passwordConfirm.addTextChangedListener(textListener(onTextChanged = { s, _, _, _ ->
                s?.let { viewModel.updateConfirmPassword(it.toString()) }
            }))

            // Обработчик нажатия клавиши "Enter" для полей ввода паролей
            passwordLast.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    passwordLast.clearFocus()
                    passwordEdit.requestFocus()
                    return@OnKeyListener true // Возврат true указывает на обработанное событие
                }
                false // Возврат false указывает на не обработанное событие
            })

            passwordEdit.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    passwordEdit.clearFocus()
                    passwordConfirm.requestFocus()
                    return@OnKeyListener true // Возврат true указывает на обработанное событие
                }
                false // Возврат false указывает на не обработанное событие
            })

            passwordConfirm.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    passwordConfirm.clearFocus()
                    passwordConfirm.hideKeyboard(activity = this@TradingPasswordActivity)
                    return@OnKeyListener true // Возврат true указывает на обработанное событие
                }
                false // Возврат false указывает на не обработанное событие
            })

            // Обработчик нажатия кнопки "Сделать сделку"
            buttonDoTrading.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.updateTradingPassword() // Обновление торгового пароля
                    this@TradingPasswordActivity.finish() // Завершение активности
                }
            }
        }
    }
}
