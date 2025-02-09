package ru.surf.learn2invest.data.services

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.surf.learn2invest.data.services.coin_icon_loader.CoinIconLoaderImpl
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.services.coin_icon_loader.CoinIconLoader

@Module
@InstallIn(SingletonComponent::class)
internal class ServicesModule {
    @Provides
    fun provideProfileManager(profileManagerImpl: ProfileManagerImpl): ProfileManager =
        profileManagerImpl

    @Provides
    fun provideCoinIconLoaderImpl(impl: CoinIconLoaderImpl): CoinIconLoader = impl
}