package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.sell_dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.DialogSellBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.CustomBottomSheetDialog
import ru.surf.learn2invest.presentation.utils.getFloatFromStringWithCurrency
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.utils.textListener
import ru.surf.learn2invest.presentation.utils.viewModelCreator
import ru.surf.learn2invest.domain.utils.withContextMAIN
import javax.inject.Inject


/**
 * Диалог продажи актива
 * @param dialogContext [Контекст открытия диалога]
 * @param lifecycleScope [Scope для выполнения асинхронных операция]
 * @param id [ID coin'а]
 * @param name [Имя (Bitcoin)]
 * @param symbol [Абревиатура (BTC)]
 */
@AndroidEntryPoint
internal class SellDialog(
    val dialogContext: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val id: String,
    private val name: String,
    private val symbol: String,
) : CustomBottomSheetDialog() {
    override val dialogTag: String = "sell"
    private var binding = DialogSellBinding.inflate(LayoutInflater.from(dialogContext))

    @Inject
    lateinit var factory: SellDialogViewModel.Factory

    private val viewModel: SellDialogViewModel by viewModelCreator {
        factory.createViewModel(id, name, symbol)
    }

    override fun initListeners() {
        binding.apply {
            lifecycleScope.launchMAIN {
                viewModel.profileFlow.collect {
                    balanceNum.text = it.fiatBalance.getWithCurrency()
                }
            }

            buttonSell.isVisible = false
            buttonSell.setOnClickListener {
                lifecycleScope.launchIO {
                    sell()
                    dismiss()
                }
            }

            imageButtonPlus.setOnClickListener {
                lifecycleScope.launchIO {
                    withContextMAIN {
                        imageButtonPlus.isEnabled = false
                    }
                    viewModel.plusLot()
                    withContextMAIN {
                        imageButtonPlus.isEnabled = true
                    }
                }
            }
            imageButtonMinus.setOnClickListener {
                lifecycleScope.launchIO {
                    withContextMAIN {
                        imageButtonMinus.isEnabled = false
                    }
                    viewModel.minusLot()
                    withContextMAIN {
                        imageButtonMinus.isEnabled = true
                    }
                }
            }

            enteringNumberOfLots.addTextChangedListener(
                textListener(afterTextChanged = {
                    lifecycleScope.launchMAIN {
                        viewModel.setLot(
                            binding.enteringNumberOfLots.text.toString().toIntOrNull() ?: 0
                        )
                    }
                })
            )
            tradingPasswordTV.addTextChangedListener(
                textListener(afterTextChanged = {
                    lifecycleScope.launchIO {
                        viewModel.setTradingPassword(binding.tradingPassword.editText?.text.toString())
                    }
                })
            )
            lifecycleScope.launchMAIN {
                viewModel.stateFlow.collect { state ->
                    val lotsData = state.lotsData
                    tradingPassword.isVisible =
                        viewModel.profileFlow.value.tradingPasswordHash != null && state.coin.amount > 0
                    val resultPrice = state.coin.coinPrice * state.lotsData.lots
                    when {
                        state.coin.amount == 0 -> {
                            buttonSell.isVisible = false
                            result.text =
                                dialogContext.getString(R.string.no_asset_for_sale)
                        }

                        state.coin.amount > 0f && lotsData.lots in 1..state.coin.amount
                            -> {
                            buttonSell.isVisible =
                                viewModel.isTrueTradingPasswordOrIsNotDefinedUseCase.invoke(
                                    profile = viewModel.profileFlow.value,
                                    password = tradingPasswordTV.text.toString()
                                )
                            result.text =
                                "${dialogContext.getString(R.string.itog)} ${resultPrice.getWithCurrency()}"
                        }

                        else -> {
                            buttonSell.isVisible = false
                            result.text = ""
                        }
                    }
                    imageButtonPlus.isVisible = lotsData.lots < state.coin.amount
                    imageButtonMinus.isVisible = lotsData.lots > 0

                    binding.priceNumber.text = state.coin.coinPrice.getWithCurrency()
                    binding.haveQuantityNumber.text = "${state.coin.amount}"

                    if (lotsData.isUpdateTVNeeded) binding.enteringNumberOfLots.setText("${lotsData.lots}")

                }
            }


        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.stopUpdatingPriceFlow()
    }

    private suspend fun sell() {
        val price = binding.priceNumber.text.toString().getFloatFromStringWithCurrency() ?: 0f
        val amountCurrent = binding.enteringNumberOfLots.text.toString().toInt()
        viewModel.sell(price, amountCurrent)
    }

    override fun getDialogView(): View {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val window = dialog.window
            if (window != null) {
                window.navigationBarColor = ContextCompat.getColor(
                    dialogContext,
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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launchIO {
            viewModel.setAssetIfInDB()
        }.invokeOnCompletion {
            viewModel.startUpdatingPriceFLow()
        }
    }
}