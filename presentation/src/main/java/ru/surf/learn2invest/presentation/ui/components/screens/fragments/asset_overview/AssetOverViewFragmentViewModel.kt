package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.usecase.GetAllCoinReviewUseCase
import ru.surf.learn2invest.domain.network.usecase.GetCoinHistoryUseCase
import ru.surf.learn2invest.presentation.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import java.text.NumberFormat
import java.util.Locale

class AssetOverViewFragmentViewModel @AssistedInject constructor(
    private val getCoinHistoryUseCase: GetCoinHistoryUseCase,
    private val getAllCoinReviewUseCase: GetAllCoinReviewUseCase,
    @Assisted var id: String
) : ViewModel() {
    private var marketCap = 0.0
    private var data = listOf<Entry>()
    private lateinit var formattedMarketCap: String
    private lateinit var formattedPrice: String
    lateinit var chartHelper: LineChartHelper
    lateinit var realTimeUpdateJob: Job
    fun loadChartData(id: String, onDataLoaded: (List<Entry>, String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = getCoinHistoryUseCase(id)) {
                is ResponseResult.Success -> {
                    data = response.value.mapIndexed { index, coinPriceResponse ->
                        Entry(index.toFloat(), coinPriceResponse.priceUsd)
                    }.toMutableList()
                    when (val coinResponse = getAllCoinReviewUseCase(id)) {
                        is ResponseResult.Success -> {
                            data =
                                data.plus(Entry(data.size.toFloat(), coinResponse.value.priceUsd))
                            marketCap = coinResponse.value.marketCapUsd.toDouble()
                            formattedMarketCap = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 0
                            }.format(marketCap) + " $"
                            formattedPrice =
                                String.format(Locale.US, "%.8f", coinResponse.value.priceUsd) + " $"
                            withContext(Dispatchers.Main) {
                                onDataLoaded(data, formattedMarketCap, formattedPrice)
                            }
                        }

                        is ResponseResult.NetworkError -> {}
                    }
                }

                is ResponseResult.NetworkError -> {}
            }
        }
    }

    fun startRealTimeUpdate(id: String, onDataLoaded: (List<Entry>, String, String) -> Unit): Job =
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(5000)
                when (val result = getAllCoinReviewUseCase(id)) {
                    is ResponseResult.Success -> {
                        if (data.isNotEmpty()) {
                            data = data.subList(0, data.lastIndex)
                                .plus(Entry(data.size.toFloat(), result.value.priceUsd))
                            marketCap = result.value.marketCapUsd.toDouble()
                            formattedMarketCap = NumberFormat.getInstance(Locale.US).apply {
                                maximumFractionDigits = 0
                            }.format(marketCap).getWithCurrency()
                            formattedPrice = String.format(Locale.US, "%.8f", result.value.priceUsd)
                                .getWithCurrency()
                            withContext(Dispatchers.Main) {
                                onDataLoaded(data, formattedMarketCap, formattedPrice)
                            }
                        }
                    }

                    is ResponseResult.NetworkError -> {}
                }
            }
        }

    @AssistedFactory
    interface Factory {
        fun createAssetOverViewFragmentViewModel(id: String): AssetOverViewFragmentViewModel
    }
}