package ru.surf.learn2invest.presentation.ui.main

import android.view.View
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.animator.usecase.AnimateAlphaUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.domain.services.ProfileManager
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val animateAlphaUseCase: AnimateAlphaUseCase,
) : ViewModel() {
    val profileFlow = profileManager.profileFlow
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    suspend fun initProfile() {
        profileManager.initProfile()
    }

    fun animateAlpha(
        view: View,
        duration: Long,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null,
        values: FloatArray,
    ) = animateAlphaUseCase.invoke(view, duration, onStart, onEnd, onCancel, onRepeat, values)
}