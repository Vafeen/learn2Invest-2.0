package ru.surf.learn2invest.domain.database.repository

interface AppDatabaseRepository {
    suspend fun clearAllTables()
}