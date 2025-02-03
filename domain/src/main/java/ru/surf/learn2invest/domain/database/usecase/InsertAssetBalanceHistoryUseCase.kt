package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.AssetBalanceHistoryRepository
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
import javax.inject.Inject


class InsertAssetBalanceHistoryUseCase @Inject constructor(
    private val repository: AssetBalanceHistoryRepository
) {
    suspend operator fun invoke(entities: List<AssetBalanceHistory>) = repository.insert(entities)
    suspend operator fun invoke(vararg entities: AssetBalanceHistory) = repository.insert(*entities)
}
