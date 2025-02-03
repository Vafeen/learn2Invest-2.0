package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.SearchedCoinRepository
import ru.surf.learn2invest.domain.domain_models.SearchedCoin
import javax.inject.Inject

class DeleteSearchedCoinUseCase @Inject constructor(
    private val repository: SearchedCoinRepository
) {
    suspend operator fun invoke(entity: SearchedCoin) = repository.delete(entity)
    suspend operator fun invoke(vararg entities: SearchedCoin) = repository.delete(*entities)
}