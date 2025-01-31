package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.surf.learn2invest.domain.domain_models.AugmentedCoinReview
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.usecase.GetAllCoinReviewUseCase
import ru.surf.learn2invest.domain.network.usecase.GetCoinHistoryUseCase
import ru.surf.learn2invest.presentation.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.launchIO
import java.text.NumberFormat
import java.util.Locale

class AssetOverViewFragmentViewModel @AssistedInject constructor(
    private val getCoinHistoryUseCase: GetCoinHistoryUseCase,
    private val getAllCoinReviewUseCase: GetAllCoinReviewUseCase,
    @Assisted var id: String
) : ViewModel() {
    private var data = listOf<Entry>()
    lateinit var chartHelper: LineChartHelper
    private var realTimeUpdateJob: Job? = null
    private val _formattedMarketCapFlow = MutableStateFlow("")
    private val _formattedPriceFlow = MutableStateFlow("")
    val formattedMarketCapFlow = _formattedMarketCapFlow.asStateFlow()
    val formattedPriceFlow = _formattedPriceFlow.asStateFlow()

    private suspend fun updateChartData(coinResponse: ResponseResult.Success<AugmentedCoinReview>) {
        data = if (data.isNotEmpty()) {
            data.subList(0, data.lastIndex)
                .plus(Entry(data.size.toFloat(), coinResponse.value.priceUsd))
        } else {
            data.plus(Entry(0f, coinResponse.value.priceUsd))
        }

        _formattedMarketCapFlow.emit(
            NumberFormat.getInstance(Locale.US).apply {
                maximumFractionDigits = 0
            }.format(coinResponse.value.marketCapUsd.toDouble()).getWithCurrency()
        )

        _formattedPriceFlow.emit(
            String.format(Locale.US, "%.8f", coinResponse.value.priceUsd).getWithCurrency()
        )

        chartHelper.updateData(data)
    }

    fun loadChartData() {
        viewModelScope.launchIO {
            val response = getCoinHistoryUseCase(id)
            if (response is ResponseResult.Success) {
                data = response.value.mapIndexed { index, coinPriceResponse ->
                    Entry(index.toFloat(), coinPriceResponse.priceUsd)
                }.toMutableList()
                val coinResponse = getAllCoinReviewUseCase(id)
                if (coinResponse is ResponseResult.Success) {
                    updateChartData(coinResponse)
                }
            }
        }
    }

    fun startRealTimeUpdate() {
        realTimeUpdateJob = viewModelScope.launchIO {
            while (true) {
                val result = getAllCoinReviewUseCase(id)
                if (result is ResponseResult.Success) updateChartData(result)
                delay(5000)
            }
        }
    }


    fun stopRealTimeUpdateJob() {
        realTimeUpdateJob?.cancel()
        realTimeUpdateJob = null
    }

    @AssistedFactory
    interface Factory {
        fun createAssetOverViewFragmentViewModel(id: String): AssetOverViewFragmentViewModel
    }
}