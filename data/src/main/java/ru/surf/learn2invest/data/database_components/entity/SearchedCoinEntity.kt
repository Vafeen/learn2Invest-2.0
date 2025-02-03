package ru.surf.learn2invest.data.database_components.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Coin'ы из поисковых запросов
 * @param id [Первичный ключ в базе данных]
 * @param coinID [Имя coin'а (Bitcoin)]
 */
@Entity
internal data class SearchedCoinEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val coinID: String
)


