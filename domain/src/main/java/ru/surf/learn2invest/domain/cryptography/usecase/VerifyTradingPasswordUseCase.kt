package ru.surf.learn2invest.domain.cryptography.usecase

import ru.surf.learn2invest.domain.cryptography.PasswordHasher
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class VerifyTradingPasswordUseCase @Inject constructor(private val passwordHasher: PasswordHasher) {
    operator fun invoke(user: Profile, tradingPassword: String): Boolean =
        passwordHasher.passwordToHash(
            firstName = user.firstName,
            lastName = user.lastName,
            password = tradingPassword
        ) == user.tradingPasswordHash
}