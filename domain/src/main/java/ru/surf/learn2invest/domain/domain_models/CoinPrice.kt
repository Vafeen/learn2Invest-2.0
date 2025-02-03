package ru.surf.learn2invest.domain.domain_models

data class CoinPrice(
    val priceUsd: Float,
    val time: Long,
    val date: String
)