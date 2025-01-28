package ru.surf.learn2invest.data.database_components.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.data.converters.TransactionConverter
import ru.surf.learn2invest.data.database_components.dao.TransactionDao
import ru.surf.learn2invest.domain.database.repository.TransactionRepository
import ru.surf.learn2invest.domain.domain_models.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionConverter: TransactionConverter
) : TransactionRepository {

    override fun getAllAsFlow(): Flow<List<Transaction>> =
        transactionDao.getAllAsFlow().map {
            transactionConverter.convertABList(it)
        }

    override suspend fun insert(entities: List<Transaction>) =
        transactionDao.insertAll(transactionConverter.convertBAList(entities))

    override suspend fun insert(vararg entities: Transaction) =
        transactionDao.insertAll(transactionConverter.convertBAList(entities.toList()))

    override suspend fun delete(entity: Transaction) =
        transactionDao.delete(transactionConverter.convertBA(entity))

    override suspend fun delete(vararg entities: Transaction) =
        transactionDao.deleteAll(transactionConverter.convertBAList(entities.toList()))

    override fun getFilteredBySymbol(symbol: String): Flow<List<Transaction>> =
        transactionDao.getFilteredBySymbol(filterSymbol = symbol).map {
            transactionConverter.convertABList(it)
        }
}
