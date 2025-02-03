package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.network_components.responses.AugmentedCoinReviewResponse
import ru.surf.learn2invest.data.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.domain.converters.OneWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.AugmentedCoinReview
import ru.surf.learn2invest.domain.domain_models.CoinReview
import javax.inject.Inject

internal class AugmentedCoinReviewConverter @Inject constructor() :
    OneWayBaseConverter<AugmentedCoinReviewResponse, AugmentedCoinReview> {
    override fun convert(a: AugmentedCoinReviewResponse): AugmentedCoinReview = AugmentedCoinReview(
        id = a.id,
        rank = a.rank,
        symbol = a.symbol,
        name = a.name,
        supply = a.supply,
        maxSupply = a.maxSupply,
        marketCapUsd = a.marketCapUsd,
        volumeUsd24Hr = a.volumeUsd24Hr,
        priceUsd = a.priceUsd,
        changePercent24Hr = a.changePercent24Hr,
        vwap24Hr = a.vwap24Hr,
        explorer = a.explorer
    )
}




