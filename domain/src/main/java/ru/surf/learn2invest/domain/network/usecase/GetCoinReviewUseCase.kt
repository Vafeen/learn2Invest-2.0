package ru.surf.learn2invest.domain.network.usecase

import ru.surf.learn2invest.domain.domain_models.AugmentedCoinReview
import ru.surf.learn2invest.domain.network.NetworkRepository
import ru.surf.learn2invest.domain.network.ResponseResult
import javax.inject.Inject

class GetCoinReviewUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend operator fun invoke(id: String): ResponseResult<AugmentedCoinReview> = repository.getCoinReview(id)
}