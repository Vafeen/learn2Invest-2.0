package ru.surf.learn2invest.data.network_components.responses

/**
 * Обертка верхнего уровня для парсинга JSON
 */
internal data class APIWrapper<T>(
    val data: T,
    val timestamp: Long
)