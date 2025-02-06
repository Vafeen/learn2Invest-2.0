package ru.surf.learn2invest.data.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.data.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.data.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.data.database_components.entity.AssetInvestEntity


@Dao
internal interface AssetInvestDao : DataAccessObject<AssetInvestEntity>,
    FlowGetAllImplementation<AssetInvestEntity> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from assetinvestentity")
    override fun getAllAsFlow(): Flow<List<AssetInvestEntity>>

    /**
     * Получение сущности по symbol
     * @param symbol [symbol, который должен быть у искомой сущности]
     */
    @Query("select * from assetinvestentity where symbol=:symbol")
    fun getBySymbol(symbol: String): Flow<AssetInvestEntity?>
}

