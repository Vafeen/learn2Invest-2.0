package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.database_components.entity.SearchedCoinEntity
import ru.surf.learn2invest.domain.converters.TwoWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.SearchedCoin
import javax.inject.Inject

internal class SearchedCoinConverter @Inject constructor() :
    TwoWayBaseConverter<SearchedCoinEntity, SearchedCoin> {

    override fun convertAB(a: SearchedCoinEntity): SearchedCoin {
        return SearchedCoin(
            id = a.id,
            coinID = a.coinID,
        )
    }

    override fun convertBA(b: SearchedCoin): SearchedCoinEntity {
        return SearchedCoinEntity(
            id = b.id,
            coinID = b.coinID,
        )
    }
}
