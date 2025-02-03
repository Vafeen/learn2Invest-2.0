package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.SearchedCoinRepository
import ru.surf.learn2invest.domain.domain_models.SearchedCoin
import javax.inject.Inject

class InsertByLimitSearchedCoinUseCase @Inject constructor(
    private val repository: SearchedCoinRepository
) {
    suspend operator fun invoke(limit: Int, entities: List<SearchedCoin>) =
        repository.insertByLimit(limit, entities)

    suspend operator fun invoke(limit: Int, vararg entities: SearchedCoin) =
        repository.insertByLimit(limit, *entities)
}