package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview

import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.utils.formatAsPrice

/**
 * Класс, содержащий финансовые результаты актива, такие как изменения стоимости и процентный результат.
 */
internal data class FinResult(val resultCost: Float, val resultPercent: Float) {
    constructor(assetInvest: AssetInvest, actualPrice: Float) : this(
        ((actualPrice * assetInvest.amount) / (assetInvest.coinPrice * assetInvest.amount)).times(
            100
        ).minus(100), ((actualPrice - assetInvest.coinPrice) * assetInvest.amount)
    )

    /**
     * Метод для получения цвета в зависимости от результата (положительный, отрицательный или нейтральный).
     */
    fun color(): Int =
        if (resultCost < 0f) R.color.error else if (resultCost > 0f) R.color.increase else R.color.black

    /**
     * Форматированная строка для отображения результата.
     */
    fun formattedStr(): String =
        "${resultCost.formatAsPrice(2)} / ${resultPercent.formatAsPrice(2)}%"
}

/**
 * Состояние информации о монете. Может быть данными или пустым результатом.
 */
internal sealed class CoinInfoState {
    data class Data(
        val finResult: FinResult, // Финансовый результат
        val coinCostResult: String = "", // Общая стоимость активов с учетом текущей цены
        val coinPriceChangesResult: String = "", // Изменения цены актива
        val coinCount: String = "", // Количество актива
    ) : CoinInfoState()

    data object EmptyResult : CoinInfoState()
}