package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog

import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.LotsData

data class BuyDialogState(
    val coin: AssetInvest,
    val lotsData: LotsData,
    val tradingPassword: String,
)
