package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.domain.utils.withContextMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.DialogBuyBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.CustomBottomSheetDialog
import ru.surf.learn2invest.presentation.utils.NoArgException
import ru.surf.learn2invest.presentation.utils.getFloatFromStringWithCurrency
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.textListener
import ru.surf.learn2invest.presentation.utils.viewModelCreator
import javax.inject.Inject

/**
 * Диалог покупки актива.
 *
 * Отображает окно покупки актива, позволяя пользователю вводить количество лотов,
 * взаимодействовать с кнопками увеличения/уменьшения лотов, вводить торговый пароль,
 * а также выполнять покупку актива при наличии достаточного баланса.
 */
@AndroidEntryPoint
internal class BuyDialog : CustomBottomSheetDialog() {
    private lateinit var binding: DialogBuyBinding
    override val dialogTag: String = "buy"

    @Inject
    lateinit var factory: BuyDialogViewModel.Factory

    /**
     * ViewModel для работы с данными о покупке актива.
     * Инициализируется с обязательными параметрами (ID, имя, символ актива).
     */
    private val viewModel by viewModelCreator {
        val id = arguments?.getString(ID_KEY) ?: throw NoArgException(ID_KEY)
        val name = arguments?.getString(NAME_KEY) ?: throw NoArgException(NAME_KEY)
        val symbol = arguments?.getString(SYMBOL_KEY) ?: throw NoArgException(SYMBOL_KEY)
        factory.createViewModel(id, name, symbol)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            updateNavigationBarColor(dialog)
        }
        return dialog
    }

    /**
     * Обновляет цвет навигационной панели в зависимости от темы приложения.
     */
    private fun updateNavigationBarColor(dialog: BottomSheetDialog) {
        val window = dialog.window
        if (window != null) {
            window.navigationBarColor = requireContext().getColor(
                if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                    R.color.sheet_background_dark
                } else {
                    R.color.white
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogBuyBinding.inflate(inflater)
        initListeners()
        return binding.root
    }

    /**
     * Инициализация обработчиков событий для элементов интерфейса.
     */
    private fun initListeners() {
        binding.apply {
            lifecycleScope.launchMAIN {
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
                    withContextMAIN { imageButtonPlus.isEnabled = false }
                    viewModel.plusLot()
                    withContextMAIN { imageButtonPlus.isEnabled = true }
                }
            }

            imageButtonMinus.setOnClickListener {
                lifecycleScope.launchIO {
                    withContextMAIN { imageButtonMinus.isEnabled = false }
                    viewModel.minusLot()
                    withContextMAIN { imageButtonMinus.isEnabled = true }
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

            lifecycleScope.launchMAIN {
                viewModel.profileFlow.collect {
                    enteringNumberOfLots.isEnabled = it.fiatBalance != 0f
                }
            }

            tradingPassword.isVisible = viewModel.profileFlow.value.tradingPasswordHash != null &&
                    viewModel.profileFlow.value.fiatBalance != 0f

            if (tradingPassword.isVisible) {
                tradingPasswordTV.addTextChangedListener(
                    textListener(afterTextChanged = {
                        lifecycleScope.launchIO {
                            viewModel.setTradingPassword(binding.tradingPassword.editText?.text.toString())
                        }
                    })
                )
            }

            lifecycleScope.launchMAIN {
                viewModel.stateFlow.collect { state ->
                    val lotsData = state.lotsData
                    val coin = state.coin
                    val willPrice = lotsData.lots * coin.coinPrice
                    val fiatBalance = viewModel.profileFlow.value.fiatBalance
                    when {
                        viewModel.isTrueTradingPasswordOrIsNotDefinedUseCase.invoke(
                            viewModel.profileFlow.value, state.tradingPassword
                        ) && lotsData.lots > 0f && fiatBalance != 0f && willPrice <= fiatBalance -> {
                            buttonBuy.isVisible = true
                            result.text =
                                "${requireContext().getString(R.string.itog)} ${willPrice.getWithCurrency()}"
                        }

                        willPrice > fiatBalance || fiatBalance == 0f -> {
                            buttonBuy.isVisible = false
                            result.text =
                                requireContext().getString(R.string.not_enough_money_for_buy)
                        }

                        else -> {
                            buttonBuy.isVisible = false
                            result.text = ""
                        }
                    }
                    val maxQuantity = maxQuantity(coin.coinPrice, state.balance)
                    maxQuantityNumber.text = "$maxQuantity"
                    imageButtonPlus.isVisible = lotsData.lots < maxQuantity
                    imageButtonMinus.isVisible = lotsData.lots > 0
                    priceNumber.text = state.coin.coinPrice.getWithCurrency()
                }
            }
        }
    }

    /**
     * Закрывает диалог и останавливает поток обновления цен.
     */
    override fun dismiss() {
        super.dismiss()
        viewModel.stopUpdatingPriceFlow()
    }

    /**
     * Выполняет покупку актива.
     */
    private suspend fun buy() {
        val price = binding.priceNumber.text.toString().getFloatFromStringWithCurrency()
        val amountCurrent = binding.enteringNumberOfLots.text.toString().toInt()
        if (price != null) viewModel.buy(price, amountCurrent)
    }

    /**
     * Рассчитывает максимальное количество лотов, доступных для покупки.
     */
    private fun maxQuantity(price: Float, balance: Float): Int = (balance / price).toInt()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launchIO {
            viewModel.setAssetIfInDB()
        }.invokeOnCompletion {
            viewModel.startUpdatingPriceFLow()
        }
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val NAME_KEY = "NAME_KEY"
        private const val SYMBOL_KEY = "SYMBOL_KEY"

        /**
         * Создаёт новый экземпляр диалога с переданными параметрами актива.
         */
        fun newInstance(id: String, name: String, symbol: String) = BuyDialog().apply {
            arguments = bundleOf(ID_KEY to id, NAME_KEY to name, SYMBOL_KEY to symbol)
        }
    }
}