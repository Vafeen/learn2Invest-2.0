package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.FragmentAssetOverviewBinding
import ru.surf.learn2invest.presentation.ui.components.chart.Last7DaysFormatter
import ru.surf.learn2invest.presentation.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.common.BaseResFragment
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.NoArgException
import ru.surf.learn2invest.presentation.utils.formatAsPrice
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.viewModelCreator
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Фрагмент, отображающий обзор актива, включая график и финансовую информацию.
 * Использует [AssetOverViewFragmentViewModel] для получения данных и отображения их на экране.
 */
@AndroidEntryPoint
internal class AssetOverviewFragment : BaseResFragment() {
    private lateinit var binding: FragmentAssetOverviewBinding

    @Inject
    lateinit var factory: AssetOverViewFragmentViewModel.Factory

    private val viewModel: AssetOverViewFragmentViewModel by viewModelCreator {
        factory.createAssetOverViewFragmentViewModel(
            id = (requireArguments().getString(AssetConstants.ID.key) ?: throw NoArgException(
                AssetConstants.ID.key
            )),
            symbol = (requireArguments().getString(AssetConstants.SYMBOL.key)
                ?: throw NoArgException(
                    AssetConstants.SYMBOL.key
                )),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)

        // Настройка и загрузка данных для графика
        viewModel.chartHelper = LineChartHelper(requireContext(), Last7DaysFormatter())
        viewModel.chartHelper.setupChart(binding.chart)
        viewModel.loadChartData()

        // Сбор данных о рыночной капитализации и форматирование их для отображения
        lifecycleScope.launchMAIN {
            viewModel.formattedMarketCapFlow.collect {
                binding.capitalisation.text = NumberFormat.getInstance(Locale.US).apply {
                    maximumFractionDigits = 0
                }.format(it).getWithCurrency()
            }
        }

        // Сбор данных о цене и форматирование их для отображения
        lifecycleScope.launchMAIN {
            viewModel.formattedPriceFlow.collect {
                binding.price.text = it.formatAsPrice(8).getWithCurrency()
            }
        }

        // Сбор и отображение информации о монете
        lifecycleScope.launchMAIN {
            viewModel.coinInfoFlow.collect { state ->
                Log.d("state", "$state")
                binding.coin.root.isVisible = state is CoinInfoState.Data
                if (state is CoinInfoState.Data) {
                    binding.coin.apply {
                        finResult.text = state.finResult.formattedStr()
                        finResult.setTextColor(getColorRes(state.finResult.color()))
                        coinsCost.text = state.coinCostResult
                        coinsPrice.text = state.coinPriceChangesResult
                        coinsCount.text = state.coinCount
                    }
                }
            }
        }

        // Определение темы (темная или светлая)
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        val accentColor =
            getColorRes(if (isDarkTheme) R.color.accent_background_dark else R.color.accent_background)

        // Установка фона для корневого элемента
        binding.coin.root.setCardBackgroundColor(accentColor)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.startRealTimeUpdate()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRealTimeUpdateJob()
    }

    companion object {
        /**
         * Создание нового экземпляра фрагмента с параметрами для ID и символа актива.
         *
         * @param id Идентификатор актива
         * @param symbol Символ актива
         * @return Новый экземпляр [AssetOverviewFragment]
         */
        fun newInstance(id: String, symbol: String): AssetOverviewFragment {
            Log.d("state", "id=$id symbol=$symbol")
            val fragment = AssetOverviewFragment()
            val args = Bundle()
            args.putString(AssetConstants.ID.key, id)
            args.putString(AssetConstants.SYMBOL.key, symbol)
            fragment.arguments = args
            return fragment
        }
    }
}
