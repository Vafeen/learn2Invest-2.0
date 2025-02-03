package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.network_components.responses.CoinReviewResponse
import ru.surf.learn2invest.domain.converters.OneWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.CoinReview
import javax.inject.Inject

internal class CoinReviewConverter @Inject constructor() :
    OneWayBaseConverter<CoinReviewResponse, CoinReview> {
    override fun convert(a: CoinReviewResponse): CoinReview = CoinReview(
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
        vwap24Hr = a.vwap24Hr
    )
}