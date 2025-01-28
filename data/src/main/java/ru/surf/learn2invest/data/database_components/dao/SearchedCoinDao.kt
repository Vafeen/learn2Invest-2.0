package ru.surf.learn2invest.data.database_components.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.surf.learn2invest.data.database_components.dao.implementation.ClearTableImplementation
import ru.surf.learn2invest.data.database_components.dao.implementation.FlowGetAllImplementation
import ru.surf.learn2invest.data.database_components.dao.parent.DataAccessObject
import ru.surf.learn2invest.data.database_components.entity.SearchedCoinEntity

@Dao
internal interface SearchedCoinDao : DataAccessObject<SearchedCoinEntity>,
    FlowGetAllImplementation<SearchedCoinEntity>,
    ClearTableImplementation {

    /**
     * Получение всех сущностей в виде Flow
     */
    @Query("select * from searchedcoinentity")
    override fun getAllAsFlow(): Flow<List<SearchedCoinEntity>>

    /**
     * Очистка таблицы
     */
    @Query("delete from searchedcoinentity")
    override suspend fun clearTable()

    /**
     *  @param limit [максимальное количество записей в БД. При потенциальном количестве записей больше этого значения, часть записей будет удалена]
     *  @param entities [Список объектов, которые нужно положить в бд]
     */
    suspend fun insertByLimit(limit: Int, entities: List<SearchedCoinEntity>) {
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
