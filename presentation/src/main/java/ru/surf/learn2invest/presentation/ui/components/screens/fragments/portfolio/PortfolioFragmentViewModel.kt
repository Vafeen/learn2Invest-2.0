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

/**
 * ViewModel для управления состоянием портфеля пользователя в приложении.
 * Отвечает за получение данных о балансе активов, их изменениях, а также за обновление
 * данных о ценах активов и вычисление изменений в портфеле.
 *
 * @param profileManager Менеджер профиля пользователя, который предоставляет доступ к данным
 *                       профиля, таким как баланс активов и фиатный баланс.
 * @param getAllAssetInvestUseCase UseCase для получения всех активов, в которые был сделан вклад.
 * @param getAllAssetBalanceHistoryUseCase UseCase для получения истории баланса активов пользователя.
 * @param insertAssetBalanceHistoryUseCase UseCase для вставки данных о балансе активов в историю.
 * @param insertByLimitAssetBalanceHistoryUseCase UseCase для вставки данных о балансе активов с ограничением.
 * @param getCoinReviewUseCase UseCase для получения данных о текущей цене актива.
 * @param getBySymbolAssetInvestUseCase UseCase для получения информации о конкретном активе по его символу.
 */
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

    // Константа для ограничения размера истории баланса
    private val HISTORY_LIMIT_SIZE = 7

    // Состояние данных графика активов
    private val _chartData = MutableStateFlow<List<Entry>>(emptyList())
    val chartData: StateFlow<List<Entry>> = _chartData

    // Баланс активов пользователя
    private val assetBalance = profileManager.profileFlow
        .map { profile -> profile.assetBalance }
        .stateIn(viewModelScope, started = SharingStarted.Lazily, 0f)

    // Баланс фиатных средств пользователя
    val fiatBalance: Flow<Float> = profileManager.profileFlow
        .map { profile -> profile.fiatBalance }
        .stateIn(viewModelScope, started = SharingStarted.Lazily, 0f)

    // Общий баланс (активы + фиат)
    val totalBalance: Flow<Float> =
        combine(assetBalance, fiatBalance) { assetBalance, fiatBalance ->
            assetBalance + fiatBalance
        }

    // Список активов
    val assetsFlow: Flow<List<AssetInvest>> = getAllAssetInvestUseCase()
        .flowOn(Dispatchers.IO)
        .onEach { assets -> loadPriceChanges(assets) }

    // Изменения цен активов
    private val _priceChanges = MutableStateFlow<Map<String, Float>>(emptyMap())
    val priceChanges: StateFlow<Map<String, Float>> get() = _priceChanges

    // Процентное изменение портфеля
    private val _portfolioChangePercentage = MutableStateFlow(0f)
    val portfolioChangePercentage: StateFlow<Float> get() = _portfolioChangePercentage

    // Задача для обновления данных в реальном времени
    private var realTimeUpdateJob: Job? = null

    /**
     * Обновляет данные портфеля, включая баланс и цены активов.
     */
    suspend fun refreshData() {
        checkAndUpdateBalanceHistory()
        loadPriceChanges(getAllAssetInvestUseCase().first())
    }

    /**
     * Получает даты всех записей в истории баланса активов.
     *
     * @return Список дат всех записей истории баланса активов.
     */
    suspend fun getAssetBalanceHistoryDates(): List<Date> =
        getAllAssetBalanceHistoryUseCase().first().map { it.date }

    /**
     * Запускает периодическое обновление данных о ценах активов.
     */
    fun startUpdatingPriceFLow() {
        realTimeUpdateJob = viewModelScope.launchIO {
            while (true) {
                refreshData()
                delay(5000)
            }
        }
    }

    /**
     * Останавливает обновление данных о ценах активов.
     */
    fun stopUpdatingPriceFlow() {
        realTimeUpdateJob?.cancel()
        realTimeUpdateJob = null
    }

    /**
     * Обновляет данные графика на основе истории баланса активов.
     */
    private suspend fun refreshChartData() {
        _chartData.value = getAllAssetBalanceHistoryUseCase().first()
            .mapIndexed { index, assetBalanceHistory ->
                Entry(index.toFloat(), assetBalanceHistory.assetBalance)
            }
    }

    /**
     * Проверяет и обновляет историю баланса активов, если необходимо.
     */
    private suspend fun checkAndUpdateBalanceHistory() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayDate = today.time

        val todayBalanceHistory = getAllAssetBalanceHistoryUseCase().first().find {
            val historyDate = Calendar.getInstance().apply {
                time = it.date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
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

    /**
     * Загружает изменения цен для всех активов.
     *
     * @param assets Список активов для которых необходимо обновить данные о ценах.
     */
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

    /**
     * Вычисляет процентное изменение портфеля.
     *
     * @param totalCurrentValue Общая текущая стоимость активов.
     * @param initialInvestment Начальная сумма инвестиций.
     */
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
