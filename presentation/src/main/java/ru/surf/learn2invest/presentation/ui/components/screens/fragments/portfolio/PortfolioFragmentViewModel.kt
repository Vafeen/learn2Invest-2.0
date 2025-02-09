package ru.surf.learn2invest.presentation.ui.components.screens.fragments.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import ru.surf.learn2invest.domain.database.usecase.GetAllAssetBalanceHistoryUseCase
import ru.surf.learn2invest.domain.database.usecase.GetAllAssetInvestUseCase
import ru.surf.learn2invest.domain.database.usecase.GetBySymbolAssetInvestUseCase
import ru.surf.learn2invest.domain.database.usecase.InsertAssetBalanceHistoryUseCase
import ru.surf.learn2invest.domain.database.usecase.InsertByLimitAssetBalanceHistoryUseCase
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.usecase.GetCoinReviewUseCase
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.utils.launchIO
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
internal class PortfolioFragmentViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val getAllAssetInvestUseCase: GetAllAssetInvestUseCase,
    private val getAllAssetBalanceHistoryUseCase: GetAllAssetBalanceHistoryUseCase,
    private val insertAssetBalanceHistoryUseCase: InsertAssetBalanceHistoryUseCase,
    private val insertByLimitAssetBalanceHistoryUseCase: InsertByLimitAssetBalanceHistoryUseCase,
    private val getCoinReviewUseCase: GetCoinReviewUseCase,
    private val getBySymbolAssetInvestUseCase: GetBySymbolAssetInvestUseCase,
) :
    ViewModel() {
    private val HISTORY_LIMIT_SIZE = 7

    private val _chartData = MutableStateFlow<List<Entry>>(emptyList())
    val chartData: StateFlow<List<Entry>> = _chartData
    private val assetBalance = profileManager.profileFlow
        .map { profile ->
            profile.assetBalance
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, 0f)

    val fiatBalance: Flow<Float> = profileManager.profileFlow
        .map { profile ->
            profile.fiatBalance
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, 0f)

    val totalBalance: Flow<Float> =
        combine(assetBalance, fiatBalance) { assetBalance, fiatBalance ->
            assetBalance + fiatBalance
        }
    val assetsFlow: Flow<List<AssetInvest>> = getAllAssetInvestUseCase()
        .flowOn(Dispatchers.IO)
        .onEach { assets ->
            loadPriceChanges(assets)
        }

    private val _priceChanges = MutableStateFlow<Map<String, Float>>(emptyMap())
    val priceChanges: StateFlow<Map<String, Float>> get() = _priceChanges

    private val _portfolioChangePercentage = MutableStateFlow(0f)
    val portfolioChangePercentage: StateFlow<Float> get() = _portfolioChangePercentage
    private var realTimeUpdateJob: Job? = null
    suspend fun refreshData() {
        checkAndUpdateBalanceHistory()
        loadPriceChanges(getAllAssetInvestUseCase().first())
    }

    suspend fun getAssetBalanceHistoryDates(): List<Date> =
        getAllAssetBalanceHistoryUseCase().first().map { it.date }


    fun startUpdatingPriceFLow() {
        realTimeUpdateJob = viewModelScope.launchIO {
            while (true) {
                refreshData()
                delay(5000)
            }
        }
    }

    fun stopUpdatingPriceFlow() {
        realTimeUpdateJob?.cancel()
        realTimeUpdateJob = null
    }

    private suspend fun refreshChartData() {
        _chartData.value = getAllAssetBalanceHistoryUseCase().first()
            .mapIndexed { index, assetBalanceHistory ->
                Entry(index.toFloat(), assetBalanceHistory.assetBalance)
            }
    }

    private suspend fun checkAndUpdateBalanceHistory() {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val todayDate = today.time

        val todayBalanceHistory = getAllAssetBalanceHistoryUseCase().first().find {
            val historyDate = Calendar.getInstance()
            historyDate.time = it.date
            historyDate.set(Calendar.HOUR_OF_DAY, 0)
            historyDate.set(Calendar.MINUTE, 0)
            historyDate.set(Calendar.SECOND, 0)
            historyDate.set(Calendar.MILLISECOND, 0)

            historyDate.time == todayDate
        }

        val totalBalance = assetBalance.first() + fiatBalance.first()

        if (todayBalanceHistory != null) {
            insertAssetBalanceHistoryUseCase(
                todayBalanceHistory.copy(
                    assetBalance = totalBalance
                )
            )
        } else {
            insertByLimitAssetBalanceHistoryUseCase(
                HISTORY_LIMIT_SIZE,
                AssetBalanceHistory(assetBalance = totalBalance, date = todayDate)
            )
        }
        refreshChartData()
    }

    private suspend fun loadPriceChanges(assets: List<AssetInvest>) {
        val priceChanges = mutableMapOf<String, Float>()
        var totalCurrentValue = 0f
        var initialInvestment = 0f
        var currentPrice: Float
        for (asset in assets) {
            val response = getCoinReviewUseCase(asset.assetID)
            if (response is ResponseResult.Success) {
                currentPrice = response.value.priceUsd
                priceChanges[asset.symbol] = currentPrice
                totalCurrentValue += currentPrice * asset.amount

                getBySymbolAssetInvestUseCase(asset.symbol).first()?.let {
                    initialInvestment += it.coinPrice * asset.amount
                }

            }
        }
        profileManager.updateProfile {
            it.copy(
                assetBalance = totalCurrentValue
            )
        }
        _priceChanges.value = priceChanges
        calculatePortfolioChangePercentage(totalCurrentValue, initialInvestment)
    }

    private suspend fun calculatePortfolioChangePercentage(
        totalCurrentValue: Float,
        initialInvestment: Float
    ) {
        if (initialInvestment != 0f) {
            val portfolioChangePercentage =
                ((totalCurrentValue + assetBalance.first()) / (initialInvestment + assetBalance.first()) - 1) * 100
            val roundedPercentage = BigDecimal(portfolioChangePercentage.toDouble())
                .setScale(2, RoundingMode.HALF_UP)
                .toFloat()
            _portfolioChangePercentage.value = roundedPercentage
        } else {
            _portfolioChangePercentage.value = 0f
        }
    }
}