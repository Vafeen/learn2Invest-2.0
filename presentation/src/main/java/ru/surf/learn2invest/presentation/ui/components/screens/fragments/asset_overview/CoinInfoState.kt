package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview

import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.utils.formatAsPrice

internal data class FinResult(val resultCost: Float, val resultPercent: Float) {
    constructor(assetInvest: AssetInvest, actualPrice: Float) : this(
        ((actualPrice * assetInvest.amount) / (assetInvest.coinPrice * assetInvest.amount)).times(
            100
        ).minus(100), ((actualPrice - assetInvest.coinPrice) * assetInvest.amount)
    )

    fun color(): Int =
        if (resultCost < 0f) R.color.error else if (resultCost > 0f) R.color.increase else R.color.black

    fun formattedStr(): String =
        "${resultCost.formatAsPrice(2)} / ${resultPercent.formatAsPrice(2)}%"
}

internal sealed class CoinInfoState {
    data class Data(
        val finResult: FinResult, // фин результат
        val coinCostResult: String = "",// общая стоимость активов (с текущей ценой)
        val coinPriceChangesResult: String = "", // старая->новая
        val coinCount: String = "",// количество
    ) : CoinInfoState()

    data object EmptyResult : CoinInfoState()
}
