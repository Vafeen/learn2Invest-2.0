package ru.surf.learn2invest.domain.services

import kotlinx.coroutines.flow.StateFlow
import ru.surf.learn2invest.domain.domain_models.Profile

interface ProfileManager {
    val profileFlow: StateFlow<Profile>
    suspend fun initProfileFlow()
    suspend fun initProfile()
    suspend fun updateProfile(updating: (Profile) -> Profile)
}