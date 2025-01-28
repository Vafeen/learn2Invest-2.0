package ru.surf.learn2invest.ui.components.screens.sign_up

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.ProfileManager
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

@HiltViewModel
class SignUpActivityViewModel @Inject constructor(private val profileManager: ProfileManager) :
    ViewModel() {
    var name: String = ""
    var lastname: String = ""
    val lengthLimit = 24
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }
}