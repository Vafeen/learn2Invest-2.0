package ru.surf.learn2invest.data.database_components.repository

import ru.surf.learn2invest.data.database_components.L2IDatabase
import ru.surf.learn2invest.domain.database.repository.AppDatabaseRepository
import javax.inject.Inject

internal class AppDatabaseRepositoryImpl @Inject constructor(private val db: L2IDatabase) : AppDatabaseRepository {
    override suspend fun clearAllTables() = db.clearAllTables()

}