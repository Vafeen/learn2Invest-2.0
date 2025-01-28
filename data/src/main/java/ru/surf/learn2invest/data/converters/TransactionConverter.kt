package ru.surf.learn2invest.data.converters

import ru.surf.learn2invest.data.database_components.entity.TransactionEntity
import ru.surf.learn2invest.domain.converters.TwoWayBaseConverter
import ru.surf.learn2invest.domain.domain_models.Transaction
import javax.inject.Inject

internal class TransactionConverter @Inject constructor() :
    TwoWayBaseConverter<TransactionEntity, Transaction> {

    override fun convertAB(a: TransactionEntity): Transaction {
        return Transaction(
            id = a.id,
            coinID = a.coinID,
            name = a.name,
            symbol = a.symbol,
            coinPrice = a.coinPrice,
            dealPrice = a.dealPrice,
            amount = a.amount,
            transactionType = a.transactionType,
        )
    }

    override fun convertBA(b: Transaction): TransactionEntity {
        return TransactionEntity(
            id = b.id,
            coinID = b.coinID,
            name = b.name,
            symbol = b.symbol,
            coinPrice = b.coinPrice,
            dealPrice = b.dealPrice,
            amount = b.amount,
            transactionType = b.transactionType,
        )
    }
}
