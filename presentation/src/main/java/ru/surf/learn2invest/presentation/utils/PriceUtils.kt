package ru.surf.learn2invest.presentation.utils

import ru.surf.learn2invest.domain.domain_models.AssetInvest

internal fun finResult(assetInvest: AssetInvest, actualPrice: Float): String {
    val resultPercent =
        ((actualPrice * assetInvest.amount) / (assetInvest.coinPrice * assetInvest.amount)).times(
            100
        ).minus(100).formatAsPrice(2)
    val resultCost = ((actualPrice - assetInvest.coinPrice) * assetInvest.amount).formatAsPrice(2)
    return "$resultCost / ${resultPercent}%"
}

internal fun priceChangesStr(assetInvest: AssetInvest, actualPrice: Float) =
    "${assetInvest.coinPrice.formatAsPrice(2)} -> ${actualPrice.formatAsPrice(2)}"
