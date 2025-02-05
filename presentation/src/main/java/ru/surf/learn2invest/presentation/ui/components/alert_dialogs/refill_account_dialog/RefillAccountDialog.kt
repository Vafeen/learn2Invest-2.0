package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.refill_account_dialog

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
 * Диалог пополнения баланса
 */
@AndroidEntryPoint
internal class RefillAccountDialog : CustomBottomSheetDialog() {
    private lateinit var binding: DialogRefillAccountBinding
    override val dialogTag: String = "refillAccount"
    private val viewModel: RefillAccountDialogViewModel by viewModels()


    private fun initListeners() {
        binding.apply {
            lifecycleScope.launchMAIN {
                viewModel.profileFlow.collect {
                    balanceTextview.text = it.fiatBalance.getWithCurrency()
                }
            }
            lifecycleScope.launchMAIN {
                viewModel.enteredBalanceFLow.collect { balanceStr ->
                    Log.d("s", balanceStr)
                    val template = requireContext().getString(R.string.enter_sum)
                    binding.apply {
                        TVEnteringSumOfBalance.text = balanceStr.ifEmpty { template }
                        val isThisTemplate = balanceStr == template

                        buttonDot.isVisible =
                            balanceStr.let { it.isNotEmpty() && !it.contains(".") }
                        backspace.isVisible = !isThisTemplate
                        buttonRefill.isVisible = balanceStr.let {
                            it.isNotEmpty() && !it.endsWith('.') && !isThisTemplate && it.toFloatOrNull()
                                ?.let { f -> f > 0 } == true
                        }
                        balanceClear.isVisible = !isThisTemplate
                        button0.visibility =
                            if (balanceStr != "0") View.VISIBLE else View.INVISIBLE // здесь так нужно, иначе верстка сломается
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
                    val button = numberButtons[index]
                    button.setOnClickListener {
                        lifecycleScope.launchMAIN {
                            button.isEnabled = false
                            viewModel.addCharToBalance("$index")
                            (it as TextView).tapOn()
                            button.isEnabled = true
                        }
                    }
                }

                buttonDot.setOnClickListener {
                    lifecycleScope.launchMAIN {
                        buttonDot.isEnabled = false
                        viewModel.addCharToBalance(".")
                        (it as TextView).tapOn()
                        buttonDot.isEnabled = true
                    }
                }

                backspace.setOnClickListener {
                    lifecycleScope.launchMAIN {
                        backspace.isEnabled = false
                        viewModel.removeLastCharFromBalance()
                        backspace.isEnabled = true
                    }
                }
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val window = dialog.window
            if (window != null) {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogRefillAccountBinding.inflate(inflater)
        initListeners()
        return binding.root
    }


    fun cancel() {
        dismiss()
    }
}