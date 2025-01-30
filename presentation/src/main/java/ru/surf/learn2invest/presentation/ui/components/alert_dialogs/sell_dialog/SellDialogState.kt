package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.sell_dialog

import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.LotsData

data class SellDialogState(
    val coin: AssetInvest,
    val lotsData: LotsData,
    val tradingPassword: String,
)