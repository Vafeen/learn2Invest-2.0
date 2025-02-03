package ru.surf.learn2invest.domain.domain_models

/**
 * Объект криптовалюты портфеля
 * @param id [Первичный ключ в базе данных]
 * @param assetID [ID coin'а]
 * @param name [Имя (Bitcoin)]
 * @param symbol [Абревиатура (BTC)]
 * @param coinPrice [Цена ]
 * @param amount [Колличество ]
 */

data class AssetInvest(
    val id: Int = 0,
    val assetID: String,
    val name: String,
    val symbol: String,
    val coinPrice: Float,
    val amount: Int,
)




