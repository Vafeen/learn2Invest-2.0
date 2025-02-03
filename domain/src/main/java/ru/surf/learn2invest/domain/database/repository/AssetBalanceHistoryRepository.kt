package ru.surf.learn2invest.domain.database.repository

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
interface AssetBalanceHistoryRepository {
    fun getAllAsFlow(): Flow<List<AssetBalanceHistory>>
    suspend fun insert(entities: List<AssetBalanceHistory>)
    suspend fun insert(vararg entities: AssetBalanceHistory)
    suspend fun insertByLimit(limit: Int, entities: List<AssetBalanceHistory>)
    suspend fun insertByLimit(limit: Int, vararg entities: AssetBalanceHistory)
    suspend fun delete(entity: AssetBalanceHistory)
    suspend fun delete(vararg entities: AssetBalanceHistory)
}
