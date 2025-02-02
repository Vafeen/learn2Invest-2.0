package ru.surf.learn2invest.data.animator

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.surf.learn2invest.domain.animator.CustomAnimator

@Module
@InstallIn(SingletonComponent::class)
internal class AnimatorModule {

    @Provides
    fun provideAnimator(impl: CustomAnimatorImpl): CustomAnimator = impl
}