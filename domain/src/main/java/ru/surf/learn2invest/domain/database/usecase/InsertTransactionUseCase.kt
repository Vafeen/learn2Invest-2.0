package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.TransactionRepository
import ru.surf.learn2invest.domain.domain_models.Transaction
import javax.inject.Inject

class InsertTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(entities: List<Transaction>) = repository.insert(entities)
    suspend operator fun invoke(vararg entities: Transaction) = repository.insert(*entities)
}