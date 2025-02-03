package ru.surf.learn2invest.data.network_components.responses

/**
 * Структура JSON для парсинга истории изменения актива
 */
internal data class CoinPriceResponse(
    val priceUsd: Float,
    val time: Long,
    val date: String
)
