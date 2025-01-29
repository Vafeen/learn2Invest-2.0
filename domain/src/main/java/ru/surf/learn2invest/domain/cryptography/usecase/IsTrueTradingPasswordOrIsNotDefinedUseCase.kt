package ru.surf.learn2invest.domain.cryptography.usecase

import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class IsTrueTradingPasswordOrIsNotDefinedUseCase @Inject constructor(
    private val verifyTradingPasswordUseCase: VerifyTradingPasswordUseCase
) {
    operator fun invoke(profile: Profile, password: String): Boolean =
        if (profile.tradingPasswordHash != null) {
            verifyTradingPasswordUseCase.invoke(user = profile, tradingPassword = password)
        } else true

}