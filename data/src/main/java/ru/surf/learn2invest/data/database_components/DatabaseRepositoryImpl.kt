package ru.surf.learn2invest.data.database_components

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.database.repository.ProfileRepository
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Репозиториq локальной базы данных для осуществления операций манипуляции с сущностями
 */
@Singleton
internal class DatabaseRepositoryImpl @Inject constructor(
    private val db: L2IDatabase,
    private val profileRepository: ProfileRepository
) {

    private var idOfProfile = 0
    lateinit var profile: Profile

    fun enableProfileFlow(lifecycleCoroutineScope: LifecycleCoroutineScope) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            profileRepository.getAllAsFlow().collect { profList ->
                if (profList.isNotEmpty()) {
                    profile = profList[idOfProfile]
                } else {
                    profile = Profile(
                        id = 0,
                        firstName = "undefined",
                        lastName = "undefined",
                        biometry = false,
                        fiatBalance = 0f,
                        assetBalance = 0f
                    )
                    profileRepository.insert(profile)
                }
            }
        }
    }

    fun clearAllTables() = db.clearAllTables()
}
