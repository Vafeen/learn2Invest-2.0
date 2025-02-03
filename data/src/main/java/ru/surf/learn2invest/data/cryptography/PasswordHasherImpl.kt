package ru.surf.learn2invest.data.cryptography

import ru.surf.learn2invest.domain.cryptography.PasswordHasher
import java.security.MessageDigest
import javax.inject.Inject

/**
 * Реализация интерфейса PasswordHasher для генерации ХЭШ-кода паролей пользователя с использованием соли.
 */
internal class PasswordHasherImpl @Inject constructor() : PasswordHasher {

    /**
     * [SHA-256](https://developer.android.com/privacy-and-security/cryptography#kotlin) - хэширование, рекомендованное Google.
     */
    private fun String.getSHA256Hash(): String =
        MessageDigest.getInstance("SHA-256").digest(toByteArray()).fold("") { str, it ->
            str + "%02x".format(it)
        }

    /**
     * Алгоритм генерации "Соли", которая нужна для того,
     * чтобы усложнить подбор пароля.
     */
    private fun String.addSaltToMessage(firstName: String, lastName: String): String =
        "${firstName}${this}${lastName}"


    /**
     * Функция получения hash'а пароля.
     *
     * @param firstName Имя пользователя, используемое для генерации соли.
     * @param lastName Фамилия пользователя, используемая для генерации соли.
     * @param password Пароль, хэш которого нужно получить.
     */
    override fun passwordToHash(
        firstName: String,
        lastName: String,
        password: String
    ): String = password.addSaltToMessage(firstName, lastName).getSHA256Hash()
}
