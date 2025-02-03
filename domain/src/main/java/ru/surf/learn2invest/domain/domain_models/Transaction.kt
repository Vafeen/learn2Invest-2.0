package ru.surf.learn2invest.domain.domain_models

import ru.surf.learn2invest.domain.TransactionsType


/**
 * Объект транзакции в истории свех транзакций
 * @param id [Первичный ключ в базе данных]
 * @param coinID [Идентификатор Coin'а для API]
 * @param name [Имя Coin'а]
 * @param symbol [Аббревиатура ]
 * @param coinPrice [Цена покупки]
 * @param dealPrice [Общая умма сделки (цена * количество)]
 * @param amount [Количество ]
 * @param transactionType [Тип транзакции (покупка/продажа)]
 */

data class Transaction(
    var id: Int = 0,
    val coinID: String,
    val name: String,
    val symbol: String,
    val coinPrice: Float,
    val dealPrice: Float,
    val amount: Int,
    val transactionType: TransactionsType
)
