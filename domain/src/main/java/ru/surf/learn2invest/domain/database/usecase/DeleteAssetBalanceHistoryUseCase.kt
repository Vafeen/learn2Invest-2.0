package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.AssetBalanceHistoryRepository
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
import javax.inject.Inject


class DeleteAssetBalanceHistoryUseCase @Inject constructor(
    private val repository: AssetBalanceHistoryRepository
) {
    suspend operator fun invoke(entity: AssetBalanceHistory) = repository.delete(entity)
    suspend operator fun invoke(vararg entities: AssetBalanceHistory) = repository.delete(*entities)
}