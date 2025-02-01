package ru.surf.learn2invest.presentation.ui.components.screens.trading_password

enum class TradingPasswordActivityActions(
    val action: String
) {
    CreateTradingPassword(action = "CreateTradingPassword"),
    ChangeTradingPassword(action = "ChangeTradingPassword"),
    RemoveTradingPassword(action = "RemoveTradingPassword");
}