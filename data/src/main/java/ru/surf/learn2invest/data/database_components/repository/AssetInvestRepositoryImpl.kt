package ru.surf.learn2invest.data.database_components.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.data.converters.AssetInvestConverter
import ru.surf.learn2invest.data.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.domain.database.repository.AssetInvestRepository
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AssetInvestRepositoryImpl @Inject constructor(
    private val assetInvestDao: AssetInvestDao,
    private val assetInvestConverter: AssetInvestConverter,
) : AssetInvestRepository {

    override fun getAllAsFlow(): Flow<List<AssetInvest>> =
        assetInvestDao.getAllAsFlow().map {
            assetInvestConverter.convertABList(it)
        }

    override suspend fun insert(entities: List<AssetInvest>) =
        assetInvestDao.insertAll(assetInvestConverter.convertBAList(entities))

    override suspend fun insert(vararg entities: AssetInvest) =
        assetInvestDao.insertAll(assetInvestConverter.convertBAList(entities.toList()))

    override suspend fun delete(entity: AssetInvest) =
        assetInvestDao.delete(assetInvestConverter.convertBA(entity))

    override suspend fun delete(vararg entities: AssetInvest) =
        assetInvestDao.deleteAll(assetInvestConverter.convertBAList(entities.toList()))

    override fun getBySymbol(symbol: String): Flow<AssetInvest?> =
        assetInvestDao.getBySymbol(symbol).map {
            Log.d("db", it.toString())
            if (it != null) assetInvestConverter.convertAB(it) else null
        }
}
