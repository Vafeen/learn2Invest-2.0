package ru.surf.learn2invest.domain.database.repository

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.domain_models.SearchedCoin
interface SearchedCoinRepository {
    fun getAllAsFlow(): Flow<List<SearchedCoin>>
    suspend fun insert(entities: List<SearchedCoin>)
    suspend fun insert(vararg entities: SearchedCoin)
    suspend fun delete(entity: SearchedCoin)
    suspend fun delete(vararg entities: SearchedCoin)
    suspend fun deleteAll()
    suspend fun insertByLimit(limit: Int, entities: List<SearchedCoin>)
    suspend fun insertByLimit(limit: Int, vararg entities: SearchedCoin)
}
