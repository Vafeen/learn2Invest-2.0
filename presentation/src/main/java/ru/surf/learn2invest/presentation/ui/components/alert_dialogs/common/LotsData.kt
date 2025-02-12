package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.common


/**
 * Данные о количестве лотов для продажи актива.
 *
 * Этот класс представляет информацию о том, сколько лотов активов пользователь собирается продать,
 * а также флаг, указывающий, нужно ли обновлять отображаемое количество лотов.
 *
 * @param lots Количество лотов, которые пользователь выбирает для продажи.
 * @param isUpdateTVNeeded Флаг, указывающий, требуется ли обновление текстового поля с количеством лотов.
 * По умолчанию установлено значение `true`.
 */
internal data class LotsData(
    val lots: Int,
    val isUpdateTVNeeded: Boolean = true,
)