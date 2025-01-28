package ru.surf.learn2invest.domain.database.usecase

import kotlinx.coroutines.flow.Flow
import ru.surf.learn2invest.domain.database.repository.SearchedCoinRepository
import ru.surf.learn2invest.domain.domain_models.SearchedCoin
import javax.inject.Inject

class GetAllSearchedCoinUseCase @Inject constructor(
    private val repository: SearchedCoinRepository
) {
    operator fun invoke(): Flow<List<SearchedCoin>> = repository.getAllAsFlow()
}








