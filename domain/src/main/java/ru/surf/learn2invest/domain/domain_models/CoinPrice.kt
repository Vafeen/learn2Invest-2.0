package ru.surf.learn2invest.domain.domain_models

/**
 * Данные о цене криптовалюты в определенный момент времени.
 *
 * @property priceUsd Цена коина в долларах США.
 * @property time Временная метка в миллисекундах с начала эпохи (Unix timestamp).
 * @property date Человеко-читаемая дата в формате (например, "YYYY-MM-DD HH:mm:ss").
 */
data class CoinPrice(
    val priceUsd: Float,
    val time: Long,
    val date: String,
)