package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog

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
import ru.surf.learn2invest.domain.ProfileManager
import ru.surf.learn2invest.domain.TransactionsType
import ru.surf.learn2invest.domain.cryptography.usecase.IsTrueTradingPasswordOrIsNotDefinedUseCase
import ru.surf.learn2invest.domain.database.usecase.GetBySymbolAssetInvestUseCase
import ru.surf.learn2invest.domain.database.usecase.InsertAssetInvestUseCase
import ru.surf.learn2invest.domain.database.usecase.InsertTransactionUseCase
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.domain.domain_models.Transaction
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.usecase.GetAllCoinReviewUseCase
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.LotsData
import ru.surf.learn2invest.presentation.utils.launchIO


class BuyDialogViewModel @AssistedInject constructor(
    private val profileManager: ProfileManager,
    private val insertTransactionUseCase: InsertTransactionUseCase,
    private val insertAssetInvestUseCase: InsertAssetInvestUseCase,
    private val getAllCoinReviewUseCase: GetAllCoinReviewUseCase,
    private val getBySymbolAssetInvestUseCase: GetBySymbolAssetInvestUseCase,
    val isTrueTradingPasswordOrIsNotDefinedUseCase: IsTrueTradingPasswordOrIsNotDefinedUseCase,
    @Assisted val id: String,
    @Assisted("name") val name: String,
    @Assisted("symbol") val symbol: String,
) : ViewModel() {
    private var realTimeUpdateJob: Job? = null
    var haveAssetsOrNot = false
    val profileFlow = profileManager.profileFlow
    private val _lotsFlow = MutableStateFlow(LotsData(0))
    val lotsFlow = _lotsFlow.asStateFlow()
    private val _tradingPasswordFlow = MutableStateFlow("")
    val tradingPasswordFlow = _tradingPasswordFlow.asStateFlow()
    private val _coinFlow = MutableStateFlow(
        AssetInvest(
            name = name, symbol = symbol, coinPrice = 0f, amount = 0f, assetID = id
        )
    )
    val stateFlow =
        combine(lotsFlow, tradingPasswordFlow, _coinFlow) { lotsData, tradingPassword, asset ->
            BuyDialogState(asset, lotsData, tradingPassword)
        }

    fun startUpdatingPriceFLow() {
        realTimeUpdateJob = viewModelScope.launchIO {
            while (true) {
                when (val result = getAllCoinReviewUseCase.invoke(_coinFlow.value.assetID)) {
                    is ResponseResult.Success -> {
                        _coinFlow.emit(_coinFlow.value.copy(coinPrice = result.value.priceUsd))
                    }

                    is ResponseResult.NetworkError -> {}
                }
                delay(5000)
            }
        }
    }

    suspend fun setAssetIfInDB() {
        getBySymbolAssetInvestUseCase.invoke(symbol = symbol)?.let {
            _coinFlow.value = it
        }
    }

    fun stopUpdatingPriceFlow() {
        realTimeUpdateJob?.cancel()
        realTimeUpdateJob = null
    }

    suspend fun setTradingPassword(password: String) {
        _tradingPasswordFlow.emit(password)
    }

    suspend fun plusLot() {
        _lotsFlow.emit(LotsData(lots = _lotsFlow.value.lots + 1))
    }

    suspend fun minusLot() {
        if (_lotsFlow.value.lots > 0)
            _lotsFlow.emit(LotsData(lots = _lotsFlow.value.lots - 1))
    }

    suspend fun setLot(lotsNumber: Int) {
        _lotsFlow.emit(LotsData(lots = lotsNumber, isUpdateTVNeeded = false))
    }

    suspend fun buy(price: Float, amountCurrent: Float) {
        val coin = _coinFlow.value
        val balance = profileFlow.value.fiatBalance
        if (balance != 0f
            && balance > price * amountCurrent
        ) {
            // обновление баланса
            profileManager.updateProfile {
                it.copy(fiatBalance = balance - price * amountCurrent)
            }

            coin.apply {
                // обновление истории
                insertTransactionUseCase.invoke(
                    Transaction(
                        coinID = assetID,
                        name = name,
                        symbol = symbol,
                        coinPrice = price,
                        dealPrice = price * amountCurrent,
                        amount = amountCurrent,
                        transactionType = TransactionsType.Buy
                    )
                )

            }
            // обновление портфеля
            if (haveAssetsOrNot) {
                insertAssetInvestUseCase.invoke(
                    coin.copy(
                        coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price)
                                / (coin.amount + amountCurrent),
                        amount = coin.amount + amountCurrent
                    )
                )
            } else {
                insertAssetInvestUseCase.invoke(
                    coin.copy(
                        coinPrice = (coin.coinPrice * coin.amount + amountCurrent * price)
                                / (coin.amount + amountCurrent),
                        amount = coin.amount + amountCurrent
                    )
                )
            }
        }
    }

    @AssistedFactory
    interface BuyFactory {
        fun createViewModel(
            @Assisted id: String,
            @Assisted("name") name: String,
            @Assisted("symbol") symbol: String,
        ): BuyDialogViewModel
    }

}


