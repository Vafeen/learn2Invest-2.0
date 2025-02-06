package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview


internal sealed class CoinInfoState {
    data class Data(
        val finResult: String = "", // фин результат
        val coinCostResult: String = "",// общая стоимость активов (с текущей ценой)
        val coinPriceChangesResult: String = "", // старая->новая
        val coinCount: String = "",// количество
    ) : CoinInfoState()

    data object EmptyResult : CoinInfoState()
}
