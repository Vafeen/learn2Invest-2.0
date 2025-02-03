package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.AssetInvestRepository
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import javax.inject.Inject


class InsertAssetInvestUseCase @Inject constructor(
    private val repository: AssetInvestRepository
) {
    suspend operator fun invoke(entities: List<AssetInvest>) = repository.insert(entities)
    suspend operator fun invoke(vararg entities: AssetInvest) = repository.insert(*entities)
}
