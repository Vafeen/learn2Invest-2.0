package ru.surf.learn2invest.domain.domain_models

/**
 * Coin'ы из поисковых запросов
 * @param id [Первичный ключ в базе данных]
 * @param coinID [Имя coin'а (Bitcoin)]
 */
data class SearchedCoin(
    var id: Int = 0,
    val coinID: String
)


