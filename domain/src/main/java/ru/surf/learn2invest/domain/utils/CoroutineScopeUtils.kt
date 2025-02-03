package ru.surf.learn2invest.domain.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun CoroutineScope.launchIO(
    block: suspend CoroutineScope.() -> Unit
) = this.launch(context = Dispatchers.IO, block = block)

fun CoroutineScope.launchMAIN(
    block: suspend CoroutineScope.() -> Unit
) = this.launch(context = Dispatchers.Main, block = block)

suspend fun <T> withContextMAIN(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.Main, block = block)

suspend fun <T> withContextIO(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.IO, block = block)

