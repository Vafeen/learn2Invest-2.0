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
import kotlinx.coroutines.flow.combine
import ru.surf.learn2invest.domain.database.usecase.GetBySymbolAssetInvestUseCase
import ru.surf.learn2invest.domain.domain_models.AugmentedCoinReview
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.usecase.GetCoinHistoryUseCase
import ru.surf.learn2invest.domain.network.usecase.GetCoinReviewUseCase
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.presentation.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.presentation.utils.formatAsPrice
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.priceChangesStr

internal class AssetOverViewFragmentViewModel @AssistedInject constructor(
    private val getCoinHistoryUseCase: GetCoinHistoryUseCase,
    private val getCoinReviewUseCase: GetCoinReviewUseCase,
    getBySymbolAssetInvestUseCase: GetBySymbolAssetInvestUseCase,
    @Assisted("id") var id: String,
    @Assisted("symbol") val symbol: String,
) : ViewModel() {
    private var data = listOf<Entry>()
    lateinit var chartHelper: LineChartHelper
    private var realTimeUpdateJob: Job? = null
    private val _formattedMarketCapFlow = MutableStateFlow(0f)
    private val _formattedPriceFlow = MutableStateFlow(0f)
    val formattedMarketCapFlow = _formattedMarketCapFlow.asStateFlow()
    val formattedPriceFlow = _formattedPriceFlow.asStateFlow()
    val coinInfoFlow =
        combine(formattedPriceFlow, getBySymbolAssetInvestUseCase.invoke(symbol)) { price, asset ->
            if (asset != null) {
                CoinInfoState.Data(
                    finResult = FinResult(asset, price),
                    coinPriceChangesResult = priceChangesStr(asset, price),
                    coinCostResult = (price * asset.amount).formatAsPrice(2).getWithCurrency(),
                    coinCount = "${asset.amount}",
                )
            } else CoinInfoState.EmptyResult
        }



    private suspend fun updateChartData(coinResponse: ResponseResult.Success<AugmentedCoinReview>) {
        data = if (data.isNotEmpty()) {
            data.subList(0, data.lastIndex)
                .plus(Entry(data.size.toFloat(), coinResponse.value.priceUsd))
        } else {
            data.plus(Entry(0f, coinResponse.value.priceUsd))
        }

        _formattedMarketCapFlow.emit(coinResponse.value.marketCapUsd)

        _formattedPriceFlow.emit(coinResponse.value.priceUsd)

        chartHelper.updateData(data)
    }

    fun loadChartData() {
        viewModelScope.launchIO {
            val response = getCoinHistoryUseCase(id)
            if (response is ResponseResult.Success) {
                data = response.value.mapIndexed { index, coinPriceResponse ->
                    Entry(index.toFloat(), coinPriceResponse.priceUsd)
                }.toMutableList()
                val coinResponse = getCoinReviewUseCase(id)
                if (coinResponse is ResponseResult.Success) {
                    updateChartData(coinResponse)
                }
            }
        }
    }

    fun startRealTimeUpdate() {
        realTimeUpdateJob = viewModelScope.launchIO {
            while (true) {
                val result = getCoinReviewUseCase(id)
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
        fun createAssetOverViewFragmentViewModel(
            @Assisted("id") id: String,
            @Assisted("symbol") symbol: String,
        ): AssetOverViewFragmentViewModel
    }
}