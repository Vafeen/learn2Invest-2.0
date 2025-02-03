package ru.surf.learn2invest.data.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.data.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.data.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.data.database_components.entity.TransactionEntity


@Dao
internal interface TransactionDao : DataAccessObject<TransactionEntity>,
    FlowGetAllImplementation<TransactionEntity> {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from transactionentity")
    override fun getAllAsFlow(): Flow<List<TransactionEntity>>

    /**
     * Получение списка всех имеющихся объектов этого типа из базы данных в виде Flow
     * @param filterSymbol [symbol, который должен быть у искомых сущностей]
     */
    @Query("SELECT * FROM transactionentity WHERE symbol = :filterSymbol")
    fun getFilteredBySymbol(filterSymbol: String): Flow<List<TransactionEntity>>
}
