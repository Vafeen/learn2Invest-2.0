package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.TransactionRepository
import ru.surf.learn2invest.domain.domain_models.Transaction
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(entity: Transaction) = repository.delete(entity)
    suspend operator fun invoke(vararg entities: Transaction) = repository.delete(*entities)
}