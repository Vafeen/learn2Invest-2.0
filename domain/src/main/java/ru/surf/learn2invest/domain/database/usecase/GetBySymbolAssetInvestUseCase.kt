package ru.surf.learn2invest.domain.database.usecase

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.database.repository.AssetInvestRepository
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import javax.inject.Inject

class GetBySymbolAssetInvestUseCase @Inject constructor(
    private val repository: AssetInvestRepository,
) {
    operator fun invoke(symbol: String): Flow<AssetInvest?> = repository.getBySymbol(symbol)
}