package ru.surf.learn2invest.domain

import ru.surf.learn2invest.domain.domain_models.AugmentedCoinReview
import ru.surf.learn2invest.domain.domain_models.CoinReview


fun AugmentedCoinReview.toCoinReview() = CoinReview(
    id = this.id,
    rank = this.rank,
    symbol = this.symbol,
    name = this.name,
    supply = this.supply,
    maxSupply = this.maxSupply,
    marketCapUsd = this.marketCapUsd,
    volumeUsd24Hr = this.volumeUsd24Hr,
    priceUsd = this.priceUsd,
    changePercent24Hr = this.changePercent24Hr,
    vwap24Hr = this.vwap24Hr
)