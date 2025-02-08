package ru.surf.learn2invest.presentation.utils

import ru.surf.learn2invest.domain.domain_models.AssetInvest


internal fun priceChangesStr(assetInvest: AssetInvest, actualPrice: Float) =
    "${assetInvest.coinPrice.formatAsPrice(2)} -> ${actualPrice.formatAsPrice(2)}"
