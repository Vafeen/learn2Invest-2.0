package ru.surf.learn2invest.presentation.ui.components.screens.trading_password

import androidx.annotation.StringRes
import ru.surf.learn2invest.presentation.R

internal enum class TradingPasswordActivityActions(
    val action: String, @StringRes val resName: Int
) {
    CreateTradingPassword(action = "CreateTradingPassword", resName = R.string.create),
    ChangeTradingPassword(action = "ChangeTradingPassword", resName = R.string.change),
    RemoveTradingPassword(action = "RemoveTradingPassword", resName =   R.string.remove);
}