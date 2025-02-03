package ru.surf.learn2invest.data.database_components.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.data.converters.SearchedCoinConverter
import ru.surf.learn2invest.data.database_components.dao.SearchedCoinDao
import ru.surf.learn2invest.domain.database.repository.SearchedCoinRepository
import ru.surf.learn2invest.domain.domain_models.SearchedCoin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchedCoinRepositoryImpl @Inject constructor(
    private val searchedCoinDao: SearchedCoinDao,
    private val searchedCoinConverter: SearchedCoinConverter
) : SearchedCoinRepository {

    override fun getAllAsFlow(): Flow<List<SearchedCoin>> =
        searchedCoinDao.getAllAsFlow().map {
            searchedCoinConverter.convertABList(it)
        }

    override suspend fun insert(entities: List<SearchedCoin>) =
        searchedCoinDao.insertAll(searchedCoinConverter.convertBAList(entities))

    override suspend fun insert(vararg entities: SearchedCoin) =
        searchedCoinDao.insertAll(searchedCoinConverter.convertBAList(entities.toList()))

    override suspend fun delete(entity: SearchedCoin) =
        searchedCoinDao.delete(searchedCoinConverter.convertBA(entity))

    override suspend fun delete(vararg entities: SearchedCoin) =
        searchedCoinDao.deleteAll(searchedCoinConverter.convertBAList(entities.toList()))

    override suspend fun deleteAll() = searchedCoinDao.clearTable()

    override suspend fun insertByLimit(limit: Int, entities: List<SearchedCoin>) =
        searchedCoinDao.insertByLimit(limit, searchedCoinConverter.convertBAList(entities))

    override suspend fun insertByLimit(limit: Int, vararg entities: SearchedCoin) =
        searchedCoinDao.insertByLimit(limit, searchedCoinConverter.convertBAList(entities.toList()))
}

