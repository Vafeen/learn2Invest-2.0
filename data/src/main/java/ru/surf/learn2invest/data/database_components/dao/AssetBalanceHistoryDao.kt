package ru.surf.learn2invest.data.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.surf.learn2invest.data.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.data.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.data.database_components.entity.AssetBalanceHistoryEntity
import java.util.Date


@Dao
internal interface AssetBalanceHistoryDao : DataAccessObject<AssetBalanceHistoryEntity>,
    FlowGetAllImplementation<AssetBalanceHistoryEntity> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from assetbalancehistoryentity")
    override fun getAllAsFlow(): Flow<List<AssetBalanceHistoryEntity>>

    @Query("SELECT * FROM AssetBalanceHistoryEntity WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: Date): AssetBalanceHistoryEntity?

    suspend fun insertByLimit(limit: Int, entities: List<AssetBalanceHistoryEntity>) {
        val coinsInDB = getAllAsFlow().first()
        val resultSize = coinsInDB.size + entities.size
        if (resultSize > limit) {
            val countToDel = coinsInDB.size + entities.size - limit
            for (index in coinsInDB.size - countToDel..<coinsInDB.size) {
                delete(coinsInDB[index])
            }
        }
        insertAll(entities)
    }
}
