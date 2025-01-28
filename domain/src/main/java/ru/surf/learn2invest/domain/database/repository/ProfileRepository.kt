package ru.surf.learn2invest.domain.database.repository

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.domain_models.Profile
interface ProfileRepository {
    fun getAllAsFlow(): Flow<List<Profile>>
    suspend fun insert(entities: List<Profile>)
    suspend fun insert(vararg entities: Profile)
    suspend fun delete(entity: Profile)
    suspend fun delete(vararg entities: Profile)
}