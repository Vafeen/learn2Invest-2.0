package ru.surf.learn2invest.data.services

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.surf.learn2invest.domain.services.ProfileManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ServicesModule {
    @Provides
    @Singleton
    fun provideProfileManager(profileManagerImpl: ProfileManagerImpl): ProfileManager =
        profileManagerImpl
}