package ru.surf.learn2invest.domain.cryptography.usecase

import ru.surf.learn2invest.domain.cryptography.PasswordHasher
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class VerifyPINUseCase @Inject constructor(private val passwordHasher: PasswordHasher) {
    operator fun invoke(user: Profile, password: String): Boolean = passwordHasher.passwordToHash(
        firstName = user.firstName,
        lastName = user.lastName,
        password = password
    ) == user.hash
}



