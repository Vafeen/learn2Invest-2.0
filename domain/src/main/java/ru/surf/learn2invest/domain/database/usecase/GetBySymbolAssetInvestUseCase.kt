package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.AssetInvestRepository
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import javax.inject.Inject

class GetBySymbolAssetInvestUseCase @Inject constructor(
    private val repository: AssetInvestRepository
) {
    suspend operator fun invoke(symbol: String): AssetInvest? = repository.getBySymbol(symbol)
}