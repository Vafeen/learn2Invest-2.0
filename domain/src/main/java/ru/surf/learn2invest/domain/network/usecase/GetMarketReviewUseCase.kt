package ru.surf.learn2invest.domain.network.usecase

import android.util.Log
import ru.surf.learn2invest.domain.domain_models.CoinReview
import ru.surf.learn2invest.domain.network.NetworkRepository
import ru.surf.learn2invest.domain.network.ResponseResult
import javax.inject.Inject

class GetMarketReviewUseCase @Inject constructor(
    private val repository: NetworkRepository,
) {
    suspend operator fun invoke(): ResponseResult<List<CoinReview>> =
        repository.getMarketReview().also {
            if (it is ResponseResult.Success)
                Log.d(
                    "data",
                    "market review data size${it.value.size}"
                )
            else Log.e("data", "market review error")
        }
}
