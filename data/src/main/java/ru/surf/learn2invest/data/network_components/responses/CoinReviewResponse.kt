package ru.surf.learn2invest.data.network_components.responses

/**
 * Ответ от API, содержащий информацию о криптовалюте (элемент массива).
 *
 * Используется для парсинга данных о рынке.
 *
 * @property id Идентификатор коина в API.
 * @property rank Ранг коина в общем списке.
 * @property symbol Аббревиатура коина (тикер).
 * @property name Полное название коина.
 * @property supply Текущий объем предложения коина.
 * @property maxSupply Максимальный объем предложения (если известен).
 * @property marketCapUsd Рыночная капитализация в долларах США.
 * @property volumeUsd24Hr Торговый объем за последние 24 часа в долларах США.
 * @property priceUsd Текущая цена коина в долларах США.
 * @property changePercent24Hr Процентное изменение цены за последние 24 часа.
 * @property vwap24Hr Средневзвешенная цена за 24 часа.
 */
internal data class CoinReviewResponse(
    val id: String,
    val rank: Int,
    val symbol: String,
    val name: String,
    val supply: Float,
    val maxSupply: Float,
    val marketCapUsd: Float,
    val volumeUsd24Hr: Float,
    val priceUsd: Float,
    val changePercent24Hr: Float,
    val vwap24Hr: Float,
)
