package ru.surf.learn2invest.domain.network.usecase

import ru.surf.learn2invest.domain.domain_models.CoinPrice
import ru.surf.learn2invest.domain.network.NetworkRepository
import ru.surf.learn2invest.domain.network.ResponseResult
import javax.inject.Inject

class GetCoinHistoryUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend operator fun invoke(id: String): ResponseResult<List<CoinPrice>> = repository.getCoinHistory(id)
}