package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.network_components.responses.CoinPriceResponse
import ru.surf.learn2invest.domain.converters.OneWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.CoinPrice
import javax.inject.Inject

internal class CoinPriceConverter @Inject constructor() : OneWayBaseConverter<CoinPriceResponse, CoinPrice> {
    override fun convert(a: CoinPriceResponse): CoinPrice = CoinPrice(
        priceUsd = a.priceUsd,
        time = a.time,
        date = a.date
    )
}