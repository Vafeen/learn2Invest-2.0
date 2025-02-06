package ru.surf.learn2invest.domain.database.repository

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.domain_models.AssetInvest

interface AssetInvestRepository {
    fun getAllAsFlow(): Flow<List<AssetInvest>>
    suspend fun insert(entities: List<AssetInvest>)
    suspend fun insert(vararg entities: AssetInvest)
    suspend fun delete(entity: AssetInvest)
    suspend fun delete(vararg entities: AssetInvest)
    fun getBySymbol(symbol: String): Flow<AssetInvest?>
}

