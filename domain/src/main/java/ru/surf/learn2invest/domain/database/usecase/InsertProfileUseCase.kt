package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.ProfileRepository
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class InsertProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(entities: List<Profile>) = repository.insert(entities)
    suspend operator fun invoke(vararg entities: Profile) = repository.insert(*entities)
}
