package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.database_components.entity.ProfileEntity
import ru.surf.learn2invest.domain.converters.TwoWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

internal class ProfileConverter @Inject constructor() :
    TwoWayBaseConverter<ProfileEntity, Profile> {

    override fun convertAB(a: ProfileEntity): Profile {
        return Profile(
            id = a.id,
            firstName = a.firstName,
            lastName = a.lastName,
            biometry = a.biometry,
            fiatBalance = a.fiatBalance,
            assetBalance = a.assetBalance,
            hash = a.hash,
            tradingPasswordHash = a.tradingPasswordHash,
        )
    }

    override fun convertBA(b: Profile): ProfileEntity {
        return ProfileEntity(
            id = b.id,
            firstName = b.firstName,
            lastName = b.lastName,
            biometry = b.biometry,
            fiatBalance = b.fiatBalance,
            assetBalance = b.assetBalance,
            hash = b.hash,
            tradingPasswordHash = b.tradingPasswordHash,
        )
    }
}
