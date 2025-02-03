package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.database_components.entity.AssetInvestEntity
import ru.surf.learn2invest.domain.converters.TwoWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import javax.inject.Inject

internal class AssetInvestConverter @Inject constructor() :
    TwoWayBaseConverter<AssetInvestEntity, AssetInvest> {

    override fun convertAB(a: AssetInvestEntity): AssetInvest {
        return AssetInvest(
            id = a.id,
            assetID = a.assetID,
            name = a.name,
            symbol = a.symbol,
            coinPrice = a.coinPrice,
            amount = a.amount,
        )
    }

    override fun convertBA(b: AssetInvest): AssetInvestEntity {
        return AssetInvestEntity(
            id = b.id,
            assetID = b.assetID,
            name = b.name,
            symbol = b.symbol,
            coinPrice = b.coinPrice,
            amount = b.amount,
        )
    }
}
