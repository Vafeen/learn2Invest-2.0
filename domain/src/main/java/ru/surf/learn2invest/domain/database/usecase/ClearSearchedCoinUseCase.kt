package ru.surf.learn2invest.domain.database.usecase

import ru.surf.learn2invest.domain.database.repository.SearchedCoinRepository
import javax.inject.Inject

class ClearSearchedCoinUseCase @Inject constructor(
    private val searchedCoinRepository: SearchedCoinRepository,
){
    suspend operator fun invoke()  = searchedCoinRepository.deleteAll()

}