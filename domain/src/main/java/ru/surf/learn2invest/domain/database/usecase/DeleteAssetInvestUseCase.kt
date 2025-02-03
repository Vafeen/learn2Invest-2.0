package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.AssetInvestRepository
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import javax.inject.Inject

class DeleteAssetInvestUseCase @Inject constructor(
    private val repository: AssetInvestRepository
) {
    suspend operator fun invoke(entity: AssetInvest) = repository.delete(entity)
    suspend operator fun invoke(vararg entities: AssetInvest) = repository.delete(*entities)
}