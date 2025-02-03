package ru.surf.learn2invest.domain.network

/**
 * Обертка для описания состояния полученных данных
 */
sealed class ResponseResult<out T> {
    data class Success<out T>(val value: T) : ResponseResult<T>()
    data object NetworkError : ResponseResult<Nothing>()
}