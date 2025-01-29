package ru.surf.learn2invest.data.network_components

import ru.surf.learn2invest.data.converters.AugmentedCoinReviewConverter
import ru.surf.learn2invest.data.converters.CoinPriceConverter
import ru.surf.learn2invest.data.converters.CoinReviewConverter
import ru.surf.learn2invest.domain.domain_models.AugmentedCoinReview
import ru.surf.learn2invest.domain.domain_models.CoinPrice
import ru.surf.learn2invest.domain.domain_models.CoinReview
import ru.surf.learn2invest.domain.network.NetworkRepository
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.RetrofitLinks
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для получения данных с API
 */

@Singleton
internal class NetworkRepositoryImpl @Inject constructor(
    private val coinAPIService: CoinAPIService,
    private val augmentedCoinReviewConverter: AugmentedCoinReviewConverter,
    private val coinPriceConverter: CoinPriceConverter,
    private val coinReviewConverter: CoinReviewConverter
) : NetworkRepository {

    override suspend fun getCoinReview(id: String): ResponseResult<AugmentedCoinReview> =
        try {
            ResponseResult.Success(
                augmentedCoinReviewConverter.convert(
                    coinAPIService.getCoinReview(
                        id = id.lowercase()
                    ).data
                )
            )
        } catch (e: Exception) {
            ResponseResult.NetworkError
        }

    override suspend fun getCoinHistory(id: String): ResponseResult<List<CoinPrice>> =
        try {
            ResponseResult.Success(
                coinPriceConverter.convertList(
                    coinAPIService.getCoinHistory(
                        id = id.lowercase(),
                        interval = RetrofitLinks.INTERVAL,
                        start = System.currentTimeMillis() - RetrofitLinks.WEEK,
                        end = System.currentTimeMillis()
                    ).data
                )
            )
        } catch (e: Exception) {
            ResponseResult.NetworkError
        }

    override suspend fun getMarketReview(): ResponseResult<List<CoinReview>> =
        try {
            ResponseResult.Success(coinReviewConverter.convertList(coinAPIService.getMarketReview().data))
        } catch (e: Exception) {
            ResponseResult.NetworkError
        }
}


