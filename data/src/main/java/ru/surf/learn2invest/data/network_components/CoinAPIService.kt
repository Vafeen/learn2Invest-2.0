package ru.surf.learn2invest.data.network_components

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.surf.learn2invest.domain.network.RetrofitLinks.API_COIN_REVIEW
import ru.surf.learn2invest.domain.network.RetrofitLinks.API_HISTORY
import ru.surf.learn2invest.domain.network.RetrofitLinks.API_MARKET_REVIEW
import ru.surf.learn2invest.data.network_components.responses.APIWrapper
import ru.surf.learn2invest.data.network_components.responses.AugmentedCoinReviewResponse
import ru.surf.learn2invest.data.network_components.responses.CoinPriceResponse
import ru.surf.learn2invest.data.network_components.responses.CoinReviewResponse

/**
 * Сервис сетевого взаимодействия
 */
internal interface CoinAPIService {

    // Получение данных о всех активах на рынке
    @GET(API_MARKET_REVIEW)
    suspend fun getMarketReview(): APIWrapper<List<CoinReviewResponse>>

    // Получение истории изменения курса конкретного актива
    @GET(API_HISTORY)
    suspend fun getCoinHistory(
        @Path("id") id: String,
        @Query("interval") interval: String,
        @Query("start") start: Long,
        @Query("end") end: Long
    ): APIWrapper<List<CoinPriceResponse>>

    // Получение подробных данных о конкретном активе
    @GET(API_COIN_REVIEW)
    suspend fun getCoinReview(
        @Path("id") id: String
    ): APIWrapper<AugmentedCoinReviewResponse>
}