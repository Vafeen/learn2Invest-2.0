package ru.surf.learn2invest.presentation.ui.components.screens.sign_in


internal data class DotsState<T>(
    val one: T,
    val two: T,
    val three: T,
    val four: T,
)

internal enum class DotState {
    NULL,
    FULL,
    RIGHT,
    ERROR
}