package ru.surf.learn2invest.domain.database.repository

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.domain_models.Transaction

interface TransactionRepository {
    fun getAllAsFlow(): Flow<List<Transaction>>
    suspend fun insert(entities: List<Transaction>)
    suspend fun insert(vararg entities: Transaction)
    suspend fun delete(entity: Transaction)
    suspend fun delete(vararg entities: Transaction)
    fun getFilteredBySymbol(symbol: String): Flow<List<Transaction>>
}
