package ru.surf.learn2invest.data.database_components.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.data.converters.AssetBalanceHistoryConverter
import ru.surf.learn2invest.data.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.domain.database.repository.AssetBalanceHistoryRepository
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
internal class AssetBalanceHistoryRepositoryImpl @Inject constructor(
    private val assetBalanceHistoryDao: AssetBalanceHistoryDao,
    private val assetBalanceHistoryConverter: AssetBalanceHistoryConverter
) : AssetBalanceHistoryRepository {

    override fun getAllAsFlow(): Flow<List<AssetBalanceHistory>> =
        assetBalanceHistoryDao.getAllAsFlow().map {
            assetBalanceHistoryConverter.convertABList(it)
        }

    override suspend fun insert(entities: List<AssetBalanceHistory>) =
        assetBalanceHistoryDao.insertAll(assetBalanceHistoryConverter.convertBAList(entities))

    override suspend fun insert(vararg entities: AssetBalanceHistory) =
        assetBalanceHistoryDao.insertAll(assetBalanceHistoryConverter.convertBAList(entities.toList()))

    override suspend fun insertByLimit(limit: Int, entities: List<AssetBalanceHistory>) =
        assetBalanceHistoryDao.insertByLimit(
            limit,
            assetBalanceHistoryConverter.convertBAList(entities)
        )

    override suspend fun insertByLimit(limit: Int, vararg entities: AssetBalanceHistory) =
        assetBalanceHistoryDao.insertByLimit(
            limit,
            assetBalanceHistoryConverter.convertBAList(entities.toList())
        )

    override suspend fun delete(entity: AssetBalanceHistory) =
        assetBalanceHistoryDao.delete(assetBalanceHistoryConverter.convertBA(entity))

    override suspend fun delete(vararg entities: AssetBalanceHistory) =
        assetBalanceHistoryDao.deleteAll(assetBalanceHistoryConverter.convertBAList(entities.toList()))
}
