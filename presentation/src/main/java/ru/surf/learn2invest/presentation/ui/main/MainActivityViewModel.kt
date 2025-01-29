package ru.surf.learn2invest.presentation.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.ProfileManager
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager
) : ViewModel() {
    val profileFlow = profileManager.profileFlow
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }
}