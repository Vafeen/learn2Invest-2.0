package ru.surf.learn2invest.data.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.database.repository.ProfileRepository
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ProfileManagerImpl @Inject constructor(private val profileRepository: ProfileRepository) :
    ProfileManager {
    private val mutex = Mutex()
    private val idOfProfile = 0
    private val defaultProfile = Profile(
        id = 0,
        firstName = "undefined",
        lastName = "undefined",
        biometry = false,
        fiatBalance = 0f,
        assetBalance = 0f
    )
    private val _profileFlow = MutableStateFlow(defaultProfile)
    override suspend fun initProfile() {
        profileRepository.getAllAsFlow().first().let { profList ->
            _profileFlow.value =
                if (profList.isNotEmpty()) profList[idOfProfile] else defaultProfile
        }
    }

    override suspend fun initProfileFlow() {
        profileRepository.getAllAsFlow().collect { profList ->
            _profileFlow.value =
                if (profList.isNotEmpty()) profList[idOfProfile] else defaultProfile
        }
    }

    override val profileFlow = _profileFlow.asStateFlow()

    override suspend fun updateProfile(updating: (Profile) -> Profile) {
        _profileFlow.value = updating(_profileFlow.value)
        mutex.withLock {
            profileRepository.insert(_profileFlow.value)
        }
    }
}