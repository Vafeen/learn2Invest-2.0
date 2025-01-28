package ru.surf.learn2invest.domain.database.usecase

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.database.repository.ProfileRepository
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class GetAllProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    operator fun invoke(): Flow<List<Profile>> = repository.getAllAsFlow()
}
