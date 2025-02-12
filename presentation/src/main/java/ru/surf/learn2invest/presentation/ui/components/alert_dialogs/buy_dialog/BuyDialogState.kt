package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog

import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.common.LotsData

/**
 * Состояние диалогового окна покупки актива.
 *
 * @property coin Текущий актив, который покупается.
 * @property lotsData Данные о количестве лотов, которые пользователь хочет купить.
 * @property tradingPassword Торговый пароль, введённый пользователем.
 * @property balance Текущий баланс пользователя в фиате.
 */
internal data class BuyDialogState(
    val coin: AssetInvest,
    val lotsData: LotsData,
    val tradingPassword: String,
    val balance: Float,
)
