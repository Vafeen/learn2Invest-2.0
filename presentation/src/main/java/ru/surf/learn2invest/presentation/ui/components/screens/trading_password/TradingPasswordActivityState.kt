package ru.surf.learn2invest.presentation.ui.components.screens.trading_password


internal enum class RuleStateKey {
    RULE_1,
    RULE_2,
    RULE_3,
    RULE_4,
    RULE_5;
}


internal data class MainActionButtonState(val isVisible: Boolean= false, val text: String= "")


