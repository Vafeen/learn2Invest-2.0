package ru.surf.learn2invest.domain.cryptography.usecase

import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class VerifyTradingPasswordUseCase @Inject constructor(private val getHashedPasswordUseCase: GetHashedPasswordUseCase) {
    operator fun invoke(user: Profile, tradingPassword: String): Boolean =
        getHashedPasswordUseCase.invoke(user, tradingPassword) == user.tradingPasswordHash
}