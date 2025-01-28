package ru.surf.learn2invest.data.database_components

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.surf.learn2invest.data.converters.AssetBalanceHistoryConverter
import ru.surf.learn2invest.data.converters.AssetInvestConverter
import ru.surf.learn2invest.data.converters.ProfileConverter
import ru.surf.learn2invest.data.converters.SearchedCoinConverter
import ru.surf.learn2invest.data.converters.TransactionConverter
import ru.surf.learn2invest.data.database_components.dao.AssetBalanceHistoryDao
import ru.surf.learn2invest.data.database_components.dao.AssetInvestDao
import ru.surf.learn2invest.data.database_components.dao.ProfileDao
import ru.surf.learn2invest.data.database_components.dao.SearchedCoinDao
import ru.surf.learn2invest.data.database_components.dao.TransactionDao
import ru.surf.learn2invest.data.database_components.repository.AppDatabaseRepositoryImpl
import ru.surf.learn2invest.data.database_components.repository.AssetBalanceHistoryRepositoryImpl
import ru.surf.learn2invest.data.database_components.repository.AssetInvestRepositoryImpl
import ru.surf.learn2invest.data.database_components.repository.ProfileRepositoryImpl
import ru.surf.learn2invest.data.database_components.repository.SearchedCoinRepositoryImpl
import ru.surf.learn2invest.data.database_components.repository.TransactionRepositoryImpl
import ru.surf.learn2invest.domain.database.repository.AppDatabaseRepository
import ru.surf.learn2invest.domain.database.repository.AssetBalanceHistoryRepository
import ru.surf.learn2invest.domain.database.repository.AssetInvestRepository
import ru.surf.learn2invest.domain.database.repository.ProfileRepository
import ru.surf.learn2invest.domain.database.repository.SearchedCoinRepository
import ru.surf.learn2invest.domain.database.repository.TransactionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseDIModule {

    @Provides
    @Singleton
    internal fun injectDatabase(@ApplicationContext context: Context): L2IDatabase =
        Room.databaseBuilder(
            context = context, klass = L2IDatabase::class.java, name = L2IDatabase.NAME
        ).build()

    @Provides
    @Singleton
    internal fun assetBalanceHistoryDao(db: L2IDatabase): AssetBalanceHistoryDao =
        db.assetBalanceHistoryDao()

    @Provides
    @Singleton
    internal fun assetInvestDao(db: L2IDatabase): AssetInvestDao = db.assetInvestDao()

    @Provides
    @Singleton
    internal fun profileDao(db: L2IDatabase): ProfileDao = db.profileDao()

    @Provides
    @Singleton
    internal fun searchedCoinDao(db: L2IDatabase): SearchedCoinDao = db.searchedCoinDao()

    @Provides
    @Singleton
    internal fun transactionDao(db: L2IDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    internal fun provideAssetBalanceHistoryRepository(
        dao: AssetBalanceHistoryDao,
        converter: AssetBalanceHistoryConverter
    ): AssetBalanceHistoryRepository = AssetBalanceHistoryRepositoryImpl(dao, converter)

    @Provides
    @Singleton
    internal fun provideAssetInvestRepository(
        dao: AssetInvestDao,
        converter: AssetInvestConverter
    ): AssetInvestRepository = AssetInvestRepositoryImpl(dao, converter)

    @Provides
    @Singleton
    internal fun provideProfileRepository(
        dao: ProfileDao,
        converter: ProfileConverter
    ): ProfileRepository = ProfileRepositoryImpl(dao, converter)

    @Provides
    @Singleton
    internal fun provideSearchedCoinRepository(
        dao: SearchedCoinDao,
        converter: SearchedCoinConverter
    ): SearchedCoinRepository = SearchedCoinRepositoryImpl(dao, converter)

    @Provides
    @Singleton
    internal fun provideTransactionRepository(
        dao: TransactionDao,
        converter: TransactionConverter
    ): TransactionRepository = TransactionRepositoryImpl(dao, converter)

    @Provides
    @Singleton
    internal fun provideAppDatabaseRepository(impl: AppDatabaseRepositoryImpl): AppDatabaseRepository =
        impl
}
