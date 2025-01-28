package ru.surf.learn2invest.data.network_components.responses

/**
 * Структура JSON для парсинга подробной информации об активе
 */
data class AugmentedCoinReviewResponse(
    val id: String, // идентификатор коина в API
    val rank: Int,
    val symbol: String, // аббревиатура
    val name: String,
    val supply: Float,
    val maxSupply: Float,
    val marketCapUsd: Float,
    val volumeUsd24Hr: Float,
    val priceUsd: Float,
    val changePercent24Hr: Float,
    val vwap24Hr: Float,
    val explorer: String
)