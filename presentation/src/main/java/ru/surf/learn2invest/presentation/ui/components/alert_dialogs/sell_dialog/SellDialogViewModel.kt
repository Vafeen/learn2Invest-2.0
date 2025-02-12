package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.sell_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import ru.surf.learn2invest.domain.TransactionsType
import ru.surf.learn2invest.domain.cryptography.usecase.IsTrueTradingPasswordOrIsNotDefinedUseCase
import ru.surf.learn2invest.domain.database.usecase.DeleteAssetInvestUseCase
import ru.surf.learn2invest.domain.database.usecase.GetBySymbolAssetInvestUseCase
import ru.surf.learn2invest.domain.database.usecase.InsertAssetInvestUseCase
import ru.surf.learn2invest.domain.database.usecase.InsertTransactionUseCase
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.domain.domain_models.Transaction
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.usecase.GetCoinReviewUseCase
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.common.LotsData


/**
 * ViewModel для управления логикой диалога продажи актива.
 *
 * Этот ViewModel отвечает за управление состоянием продажи актива, включая цену, количество лотов и торговый пароль.
 * Он также управляет обновлением данных актива и выполнением операций продажи.
 *
 * @param profileManager Менеджер профиля пользователя.
 * @param insertTransactionUseCase UseCase для добавления транзакции.
 * @param insertAssetInvestUseCase UseCase для добавление актива.
 * @param deleteAssetInvestUseCase UseCase для удаления активов из инвестиций.
 * @param getCoinReviewUseCase UseCase для получения информации о текущей цене актива.
 * @param getBySymbolAssetInvestUseCase UseCase для получения активов по символу.
 * @param isTrueTradingPasswordOrIsNotDefinedUseCase UseCase для проверки торгового пароля.
 * @param id Идентификатор актива.
 * @param name Название актива.
 * @param symbol Символ актива.
 */
internal class SellDialogViewModel @AssistedInject constructor(
    private val profileManager: ProfileManager,
    private val insertTransactionUseCase: InsertTransactionUseCase,
    private val insertAssetInvestUseCase: InsertAssetInvestUseCase,
    private val deleteAssetInvestUseCase: DeleteAssetInvestUseCase,
    private val getCoinReviewUseCase: GetCoinReviewUseCase,
    private val getBySymbolAssetInvestUseCase: GetBySymbolAssetInvestUseCase,
    val isTrueTradingPasswordOrIsNotDefinedUseCase: IsTrueTradingPasswordOrIsNotDefinedUseCase,
    @Assisted("id") val id: String,
    @Assisted("name") val name: String,
    @Assisted("symbol") val symbol: String,
) : ViewModel() {

    /**
     * Задержка для обновления реальной цены актива.
     */
    private var realTimeUpdateJob: Job? = null

    /**
     * Поток, содержащий информацию о профиле пользователя.
     */
    val profileFlow = profileManager.profileFlow

    /**
     * Поток, содержащий данные о лотах для продажи.
     */
    private val _lotsFlow = MutableStateFlow(LotsData(0))

    /**
     * Поток, содержащий данные о торговом пароле.
     */
    private val _tradingPasswordFlow = MutableStateFlow("")

    /**
     * Поток, содержащий информацию об активах для продажи.
     */
    private val _coinFlow = MutableStateFlow(
        AssetInvest(
            name = name, symbol = symbol, coinPrice = 0f, amount = 0, assetID = id
        )
    )

    /**
     * Состояние диалога продажи, которое объединяет информацию о лотах, торговом пароле и активе.
     */
    val stateFlow =
        combine(_lotsFlow.asStateFlow(), _tradingPasswordFlow.asStateFlow(), _coinFlow) { lotsData, tradingPassword, asset ->
            SellDialogState(asset, lotsData, tradingPassword)
        }

    /**
     * Увеличивает количество лотов на 1.
     */
    suspend fun plusLot() {
        _lotsFlow.emit(LotsData(lots = _lotsFlow.value.lots + 1))
    }

    /**
     * Уменьшает количество лотов на 1, если количество лотов больше 0.
     */
    suspend fun minusLot() {
        if (_lotsFlow.value.lots > 0)
            _lotsFlow.emit(LotsData(lots = _lotsFlow.value.lots - 1))
    }

    /**
     * Устанавливает количество лотов для продажи.
     *
     * @param lotsNumber Новое количество лотов.
     */
    suspend fun setLot(lotsNumber: Int) {
        _lotsFlow.emit(LotsData(lots = lotsNumber, isUpdateTVNeeded = false))
    }

    /**
     * Устанавливает значение торгового пароля.
     *
     * @param password Торговый пароль.
     */
    suspend fun setTradingPassword(password: String) {
        _tradingPasswordFlow.emit(password)
    }

    /**
     * Выполняет операцию продажи актива.
     *
     * @param price Цена продажи актива.
     * @param amountCurrent Количество актива для продажи.
     */
    suspend fun sell(price: Float, amountCurrent: Int) {
        val coin = _coinFlow.value

        profileManager.updateProfile {
            it.copy(fiatBalance = it.fiatBalance + price * amountCurrent)
        }

        coin.apply {
            // Обновление истории транзакций
            insertTransactionUseCase(
                Transaction(
                    coinID = assetID,
                    name = name,
                    symbol = symbol,
                    coinPrice = price,
                    dealPrice = price * amountCurrent,
                    amount = amountCurrent,
                    transactionType = TransactionsType.Sell
                )
            )
        }

        // Обновление портфеля
        if (amountCurrent < coin.amount) {
            insertAssetInvestUseCase(
                coin.copy(
                    coinPrice = (coin.coinPrice * coin.amount - amountCurrent * price) / (coin.amount - amountCurrent),
                    amount = coin.amount - amountCurrent
                )
            )
        } else deleteAssetInvestUseCase(coin)
    }

    /**
     * Запускает обновление цены актива каждые 5 секунд.
     */
    fun startUpdatingPriceFLow() {
        realTimeUpdateJob = viewModelScope.launchIO {
            while (true) {
                when (val result = getCoinReviewUseCase.invoke(_coinFlow.value.assetID)) {
                    is ResponseResult.Success -> {
                        _coinFlow.emit(_coinFlow.value.copy(coinPrice = result.value.priceUsd))
                    }

                    is ResponseResult.NetworkError -> {}
                }
                delay(5000)
            }
        }
    }

    /**
     * Останавливает обновление цены актива.
     */
    fun stopUpdatingPriceFlow() {
        realTimeUpdateJob?.cancel()
        realTimeUpdateJob = null
    }

    /**
     * Устанавливает актив, если он найден в базе данных.
     */
    suspend fun setAssetIfInDB() {
        getBySymbolAssetInvestUseCase.invoke(symbol = symbol).first()?.let {
            _coinFlow.value = it
        }
    }

    /**
     * Фабрика для создания экземпляров [SellDialogViewModel].
     */
    @AssistedFactory
    interface Factory {
        /**
         * Создает экземпляр [SellDialogViewModel] с заданными параметрами.
         *
         * @param id Идентификатор актива.
         * @param name Название актива.
         * @param symbol Символ актива.
         * @return Экземпляр [SellDialogViewModel].
         */
        fun createViewModel(
            @Assisted("id") id: String,
            @Assisted("name") name: String,
            @Assisted("symbol") symbol: String,
        ): SellDialogViewModel
    }
}
