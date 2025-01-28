package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.database_components.entity.AssetBalanceHistoryEntity
import ru.surf.learn2invest.domain.converters.TwoWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.AssetBalanceHistory
import javax.inject.Inject

internal class AssetBalanceHistoryConverter @Inject constructor() :
    TwoWayBaseConverter<AssetBalanceHistoryEntity, AssetBalanceHistory> {
    override fun convertAB(a: AssetBalanceHistoryEntity): AssetBalanceHistory = AssetBalanceHistory(
        id = a.id,
        assetBalance = a.assetBalance,
        date = a.date
    )

    override fun convertBA(b: AssetBalanceHistory): AssetBalanceHistoryEntity =
        AssetBalanceHistoryEntity(
            id = b.id,
            assetBalance = b.assetBalance,
            date = b.date
        )

}