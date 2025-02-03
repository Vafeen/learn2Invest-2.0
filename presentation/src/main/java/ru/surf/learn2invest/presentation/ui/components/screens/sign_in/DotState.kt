package ru.surf.learn2invest.presentation.ui.components.screens.sign_in


internal data class DotsState<T>(
    val one: T,
    val two: T,
    val three: T,
    val four: T,
) {
    fun asList(): List<T> = listOf(one, two, three, four)
}

internal enum class DotState {
    NULL,
    FULL,
    RIGHT,
    ERROR
}