package ru.surf.learn2invest.domain.database.usecase

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.database.repository.AssetBalanceHistoryRepository
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
import javax.inject.Inject

class GetAllAssetBalanceHistoryUseCase @Inject constructor(
    private val repository: AssetBalanceHistoryRepository
) {
    operator fun invoke(): Flow<List<AssetBalanceHistory>> = repository.getAllAsFlow()
}

