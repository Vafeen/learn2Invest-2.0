package ru.surf.learn2invest.domain.domain_models


/**
 * Профиль пользователя со всеми настройками
 * @param id [Первичный ключ в базе данных]
 * @param firstName [Имя ]
 * @param lastName [Фамилия ]
 * @param biometry  [Вкл/Выкл входа по биометрии]
 * @param fiatBalance  [Баланс обычных денег]
 * @param assetBalance  [Суммарная стоимость активов ]
 * @param hash   [Хэш PIN-кода]
 * @param tradingPasswordHash   [Хэш Торгового пароля]
 */

data class Profile(
    var id: Int = 0,
    val firstName: String,
    val lastName: String,
    val biometry: Boolean,
    val fiatBalance: Float,
    val assetBalance: Float,
    val hash: String? = null,
    val tradingPasswordHash: String? = null
)

