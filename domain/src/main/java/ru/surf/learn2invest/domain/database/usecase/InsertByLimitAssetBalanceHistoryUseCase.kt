package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.AssetBalanceHistoryRepository
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
import javax.inject.Inject


class InsertByLimitAssetBalanceHistoryUseCase @Inject constructor(
    private val repository: AssetBalanceHistoryRepository
) {
    suspend operator fun invoke(limit: Int, entities: List<AssetBalanceHistory>) =
        repository.insertByLimit(limit, entities)

    suspend operator fun invoke(limit: Int, vararg entities: AssetBalanceHistory) =
        repository.insertByLimit(limit, *entities)
}
