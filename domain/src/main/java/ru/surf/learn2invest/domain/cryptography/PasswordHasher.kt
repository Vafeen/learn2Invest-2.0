package ru.surf.learn2invest.domain.cryptography
/**
 * Интерфейс для генерации ХЭШ-кода паролей пользователя с использованием соли.
 */
interface PasswordHasher {
    /**
     * Генерирует ХЭШ пароля с добавлением соли, основанной на имени и фамилии пользователя.
     *
     * @param firstName Имя пользователя, используемое для генерации соли.
     * @param lastName Фамилия пользователя, используемая для генерации соли.
     * @param password Пароль, который нужно захэшировать.
     * @return Хэшированный пароль.
     */
    fun passwordToHash(
        firstName: String,
        lastName: String,
        password: String
    ): String
}
