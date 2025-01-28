package ru.surf.learn2invest.domain.database.usecase

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.database.repository.TransactionRepository
import ru.surf.learn2invest.domain.domain_models.Transaction
import javax.inject.Inject

class GetFilteredBySymbolTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(symbol: String): Flow<List<Transaction>> =
        repository.getFilteredBySymbol(symbol)
}