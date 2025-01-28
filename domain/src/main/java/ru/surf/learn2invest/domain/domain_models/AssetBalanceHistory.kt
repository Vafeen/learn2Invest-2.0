package ru.surf.learn2invest.domain.domain_models

import java.util.Date


/**
 * Объект баланса портфеля
 * @param id [Первичный ключ в базе данных]
 * @param assetBalance [Общий баланс портфеля]
 * @param date [Дата, когда был такой баланс]
 */
data class AssetBalanceHistory( // баланс портфеля
    val id: Int = 0,
    val assetBalance: Float, // стоимость портфеля
    val date: Date // поле с датой
)
