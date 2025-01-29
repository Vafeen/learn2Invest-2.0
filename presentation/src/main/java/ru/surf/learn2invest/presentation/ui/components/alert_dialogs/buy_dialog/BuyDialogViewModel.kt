package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import javax.inject.Inject


@HiltViewModel
class BuyDialogViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val insertTransactionUseCase: InsertTransactionUseCase,
    private val insertAssetInvestUseCase: InsertAssetInvestUseCase,
    private val getAllCoinReviewUseCase: GetAllCoinReviewUseCase,
    val getBySymbolAssetInvestUseCase: GetBySymbolAssetInvestUseCase,
    val isTrueTradingPasswordOrIsNotDefinedUseCase: IsTrueTradingPasswordOrIsNotDefinedUseCase
) : ViewModel() {
    lateinit var realTimeUpdateJob: Job
    var haveAssetsOrNot = false
    lateinit var coin: AssetInvest
    val profileFlow = profileManager.profileFlow


    suspend fun buy(price: Float, amountCurrent: Float) {
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


    fun startRealTimeUpdate(onUpdateFields: (result: String) -> Unit): Job =
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                when (val result = getAllCoinReviewUseCase.invoke(coin.assetID)) {
                    is ResponseResult.Success -> {
                        onUpdateFields(result.value.priceUsd.getWithCurrency())
                    }

                    is ResponseResult.NetworkError -> {}
                }
                delay(5000)
            }
        }
}
