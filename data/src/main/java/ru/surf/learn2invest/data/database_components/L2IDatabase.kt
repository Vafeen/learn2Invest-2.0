package ru.surf.learn2invest.data.database_components

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.surf.learn2invest.data.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.data.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.data.database_components.dao.ProfileDao
import ru.surf.learn2invest.data.database_components.dao.SearchedCoinDao
import ru.surf.learn2invest.data.database_components.dao.TransactionDao
import ru.surf.learn2invest.data.database_components.entity.AssetBalanceHistoryEntity
import ru.surf.learn2invest.data.database_components.entity.AssetInvestEntity
import ru.surf.learn2invest.data.database_components.entity.ProfileEntity
import ru.surf.learn2invest.data.database_components.entity.SearchedCoinEntity
import ru.surf.learn2invest.data.database_components.entity.TransactionEntity

@Database(
    exportSchema = true,
    entities = [
        AssetBalanceHistoryEntity::class,
        AssetInvestEntity::class,
        ProfileEntity::class,
        SearchedCoinEntity::class,
        TransactionEntity::class,
    ], version = 1
)
/**
 * Локальная база данных для осуществления операций манипуляции с сущностями
 */
@TypeConverters(Converters::class)
internal abstract class L2IDatabase : RoomDatabase() {
    companion object {
        const val NAME = "learn2investDatabase.db"
    }

    abstract fun assetBalanceHistoryDao(): AssetBalanceHistoryDao
    abstract fun assetInvestDao(): AssetInvestDao
    abstract fun profileDao(): ProfileDao
    abstract fun searchedCoinDao(): SearchedCoinDao
    abstract fun transactionDao(): TransactionDao
}