package ru.surf.learn2invest.domain.network.usecase

import ru.surf.learn2invest.domain.domain_models.CoinReview
import ru.surf.learn2invest.domain.network.NetworkRepository
import ru.surf.learn2invest.domain.network.ResponseResult
import javax.inject.Inject

class GetMarketReviewUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend operator fun invoke(): ResponseResult<List<CoinReview>> = repository.getMarketReview()
}
