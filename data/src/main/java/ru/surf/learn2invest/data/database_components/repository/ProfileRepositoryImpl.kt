package ru.surf.learn2invest.data.database_components.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.data.converters.ProfileConverter
import ru.surf.learn2invest.data.database_components.dao.ProfileDao
import ru.surf.learn2invest.domain.database.repository.ProfileRepository
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
internal class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val profileConverter: ProfileConverter
) : ProfileRepository {

    override fun getAllAsFlow(): Flow<List<Profile>> =
        profileDao.getAllAsFlow().map {
            profileConverter.convertABList(it)
        }

    override suspend fun insert(entities: List<Profile>) =
        profileDao.insertAll(profileConverter.convertBAList(entities))

    override suspend fun insert(vararg entities: Profile) =
        profileDao.insertAll(profileConverter.convertBAList(entities.toList()))

    override suspend fun delete(entity: Profile) =
        profileDao.delete(profileConverter.convertBA(entity))

    override suspend fun delete(vararg entities: Profile) =
        profileDao.deleteAll(profileConverter.convertBAList(entities.toList()))
}
