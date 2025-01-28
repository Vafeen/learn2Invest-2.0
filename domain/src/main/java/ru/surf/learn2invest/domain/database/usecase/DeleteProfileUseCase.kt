package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.ProfileRepository
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(entity: Profile) = repository.delete(entity)
    suspend operator fun invoke(vararg entities: Profile) = repository.delete(*entities)
}
