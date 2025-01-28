package ru.surf.learn2invest.domain

/**
 * Класс типов транзакции
 **/

enum class TransactionsType(val action: Int) {
    Buy(action = 1),
    Sell(action = 0)
}