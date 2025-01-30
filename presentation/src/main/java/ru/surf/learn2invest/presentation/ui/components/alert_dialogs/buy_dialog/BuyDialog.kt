package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.DialogBuyBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.CustomBottomSheetDialog
import ru.surf.learn2invest.presentation.utils.getFloatFromStringWithCurrency
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.launchIO
import ru.surf.learn2invest.presentation.utils.launchMAIN
import ru.surf.learn2invest.presentation.utils.textListener
import ru.surf.learn2invest.presentation.utils.viewModelCreator
import ru.surf.learn2invest.presentation.utils.withContextMAIN
import javax.inject.Inject


/**
 * Диалог покупки актива
 * @param dialogContext [Контекст открытия диалога]
 * @param id [ID coin'а]
 * @param name [Имя (Bitcoin)]
 * @param symbol [Абревиатура (BTC)]
 */

@AndroidEntryPoint
class BuyDialog(
    private val dialogContext: Context,
    id: String,
    name: String,
    symbol: String,
) : CustomBottomSheetDialog() {
    private var binding = DialogBuyBinding.inflate(LayoutInflater.from(dialogContext))
    override val dialogTag: String = "buy"
    @Inject
    lateinit var buyFactory: BuyDialogViewModel.BuyFactory
    private val viewModel by viewModelCreator {
        buyFactory.createViewModel(id, name, symbol)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            updateNavigationBarColor(dialog)
        }
        return dialog
    }

    private fun updateNavigationBarColor(dialog: BottomSheetDialog) {
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

    override fun initListeners() {
        binding.apply {
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.profileFlow.collect {
                    balanceNum.text = it.fiatBalance.getWithCurrency()
                }
            }


            buttonBuy.isVisible = false

            buttonBuy.setOnClickListener {
                lifecycleScope.launchIO {
                    buy()
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
//                enteringNumberOfLots.setText(enteringNumberOfLots.text.let { numOfLotsText ->
//                    (numOfLotsText.toString().toIntOrNull() ?: 0).let {
//                        val balance = viewModel.profileFlow.value.fiatBalance
//                        when {
//                            resultPrice(onFuture = true) <= balance -> {
//                                (it + 1).toString()
//                            }
//
//                            else -> {
//                                it.toString()
//                            }
//                        }
//                    }
//                })
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
//                enteringNumberOfLots.setText(enteringNumberOfLots.text.let { text ->
//                    text.toString().toIntOrNull()?.let {
//                        when {
//                            it == 1 || it == 0 -> {
//                                ""
//                            }
//
//                            it > 1 -> {
//                                (it - 1).toString()
//                            }
//
//                            else -> {
//                                text
//                            }
//                        }
//                    }
//                })
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
            lifecycleScope.launchMAIN {
                viewModel.profileFlow.collect {
                    enteringNumberOfLots.isEnabled = it.fiatBalance != 0f
                }
            }
            val mutex = Mutex()
            tradingPassword.isVisible =
                if (viewModel.profileFlow.value.tradingPasswordHash != null && viewModel.profileFlow.value.fiatBalance != 0f) {
                    tradingPasswordTV.addTextChangedListener(
                        textListener(afterTextChanged = {
                            lifecycleScope.launchIO {
                                mutex.withLock {
                                    viewModel.setTradingPassword(binding.tradingPassword.editText?.text.toString())
                                }
                            }
                        })
                    )
                    true
                } else false

            lifecycleScope.launchMAIN {
                viewModel.stateFlow.collect { state ->
                    val willPrice = resultPrice(onFuture = false)
                    val fiatBalance = viewModel.profileFlow.value.fiatBalance
                    when {
                        (viewModel.isTrueTradingPasswordOrIsNotDefinedUseCase.invoke(
                            viewModel.profileFlow.value,
                            state.tradingPassword
                        ) && state.lotsData.lots > 0 && fiatBalance != 0f &&
                                willPrice <= fiatBalance
                                ) -> {
                            binding.buttonBuy.isVisible = true
                            result.text =
                                "${dialogContext.getString(R.string.itog)} ${willPrice.getWithCurrency()} "
                        }

                        (willPrice > fiatBalance || fiatBalance == 0f) -> {
                            buttonBuy.isVisible = false
                            result.text = dialogContext.getString(R.string.not_enough_money_for_buy)
                        }

                        else -> {
                            buttonBuy.isVisible = false
                            result.text = ""
                        }
                    }

                    binding.priceNumber.text = state.coin.coinPrice.getWithCurrency()
                }
            }
            lifecycleScope.launchMAIN {
                viewModel.lotsFlow.collect { lotsData ->
                    if (lotsData.isUpdateTVNeeded) binding.enteringNumberOfLots.setText("${lotsData.lots}")
                    imageButtonPlus.isVisible =
                        resultPrice(onFuture = true) <= viewModel.profileFlow.value.fiatBalance
                    imageButtonMinus.isVisible = lotsData.lots > 0
                    Log.d("lots", lotsData.toString())
                }
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.stopUpdatingPriceFlow()
    }

    private suspend fun buy() {
        val price =
            binding.priceNumber.text.toString().getFloatFromStringWithCurrency() ?: throw Exception(
                "Price is null for Buy in BuyDialog"
            )
        val amountCurrent = binding.enteringNumberOfLots.text.toString().toInt().toFloat()
        viewModel.buy(amountCurrent = amountCurrent, price = price)
    }

    override fun getDialogView(): View {
        return binding.root
    }


    private fun resultPrice(
        onFuture: Boolean
    ): Float {
        binding.apply {
            val priceText = priceNumber.text.toString()
            val price = priceText.getFloatFromStringWithCurrency() ?: 0f
            val number = enteringNumberOfLots.text.toString().toIntOrNull() ?: 0
            return price * (number + if (onFuture) 1 else 0)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.setAssetIfInDB()
        }.invokeOnCompletion {
            viewModel.startUpdatingPriceFLow()
        }
    }
}
