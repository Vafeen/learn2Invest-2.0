package ru.surf.learn2invest.domain.cryptography.usecase

import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

class VerifyPINUseCase @Inject constructor(private val getHashedPasswordUseCase: GetHashedPasswordUseCase) {
    operator fun invoke(user: Profile, password: String): Boolean =
        getHashedPasswordUseCase.invoke(user, password) == user.hash
}



