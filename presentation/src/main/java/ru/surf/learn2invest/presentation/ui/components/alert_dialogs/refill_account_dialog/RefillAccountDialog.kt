package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.refill_account_dialog

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.domain.utils.tapOn
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.DialogRefillAccountBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.CustomBottomSheetDialog
import ru.surf.learn2invest.presentation.utils.getWithCurrency

/**
 * Диалоговое окно для пополнения баланса.
 * Отображается в виде BottomSheetDialog и позволяет пользователю вводить сумму пополнения.
 */
@AndroidEntryPoint
internal class RefillAccountDialog : CustomBottomSheetDialog() {

    private lateinit var binding: DialogRefillAccountBinding
    override val dialogTag: String = "refillAccount"
    private val viewModel: RefillAccountDialogViewModel by viewModels()

    /**
     * Инициализация слушателей для обработки пользовательских действий в диалоговом окне.
     */
    private fun initListeners() {
        binding.apply {
            lifecycleScope.launchMAIN {
                viewModel.profileFlow.collect { profile ->
                    balanceTextview.text = profile.fiatBalance.getWithCurrency()
                }
            }

            lifecycleScope.launchMAIN {
                viewModel.enteredBalanceFLow.collect { balanceStr ->
                    val template = requireContext().getString(R.string.enter_sum)
                    binding.apply {
                        TVEnteringSumOfBalance.text = balanceStr.ifEmpty { template }
                        val isTemplate = balanceStr == template

                        buttonDot.isVisible = balanceStr.isNotEmpty() && !balanceStr.contains(".")
                        backspace.isVisible = !isTemplate
                        buttonRefill.isVisible = balanceStr.let {
                            it.isNotEmpty() && !it.endsWith('.') && !isTemplate && it.toFloatOrNull()
                                ?.let { amount -> amount > 0 } == true
                        }
                        balanceClear.isVisible = !isTemplate
                        button0.visibility = if (balanceStr != "0") View.VISIBLE else View.INVISIBLE
                    }
                }
            }

            balanceClear.setOnClickListener {
                lifecycleScope.launchMAIN {
                    balanceClear.isEnabled = false
                    viewModel.clearBalance()
                    balanceClear.isEnabled = true
                }
            }

            buttonRefill.setOnClickListener {
                lifecycleScope.launchIO {
                    viewModel.refill()
                    cancel()
                }
            }

            setupNumberPad()
        }
    }

    /**
     * Настраивает обработку нажатий на цифровую клавиатуру.
     */
    private fun setupNumberPad() {
        val numberButtons = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9,
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                lifecycleScope.launchMAIN {
                    button.isEnabled = false
                    viewModel.addCharToBalance("$index")
                    (it as TextView).tapOn()
                    button.isEnabled = true
                }
            }
        }

        binding.buttonDot.setOnClickListener {
            lifecycleScope.launchMAIN {
                binding.buttonDot.isEnabled = false
                viewModel.addCharToBalance(".")
                (it as TextView).tapOn()
                binding.buttonDot.isEnabled = true
            }
        }

        binding.backspace.setOnClickListener {
            lifecycleScope.launchMAIN {
                binding.backspace.isEnabled = false
                viewModel.removeLastCharFromBalance()
                binding.backspace.isEnabled = true
            }
        }
    }

    /**
     * Создаёт и настраивает диалоговое окно.
     * Изменяет цвет навигационной панели в зависимости от текущей темы устройства.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            dialog.window?.let { window ->
                window.navigationBarColor = ContextCompat.getColor(
                    requireContext(),
                    if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                        R.color.sheet_background_dark
                    } else {
                        R.color.white
                    }
                )
            }
        }
        return dialog
    }

    /**
     * Создаёт представление диалогового окна.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogRefillAccountBinding.inflate(inflater)
        initListeners()
        return binding.root
    }

    /**
     * Закрывает диалоговое окно.
     */
    fun cancel() {
        dismiss()
    }
}