package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.SearchedCoinRepository
import javax.inject.Inject

class DeleteAllSearchedCoinUseCase @Inject constructor(
    private val repository: SearchedCoinRepository
) {
    suspend operator fun invoke() = repository.deleteAll()
}