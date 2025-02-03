package ru.surf.learn2invest.presentation.ui.components.screens.sign_up

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.domain_models.Profile
import javax.inject.Inject

@HiltViewModel
internal class SignUpActivityViewModel @Inject constructor(private val profileManager: ProfileManager) :
    ViewModel() {
    private val _firstnameFlow = MutableStateFlow("")
    private val _lastNameFlow = MutableStateFlow("")
    val firstnameFlow = _firstnameFlow.asStateFlow()
    val lastNameFlow = _lastNameFlow.asStateFlow()
    val stateFlow = combine(firstnameFlow, lastNameFlow) { firstname, lastname ->
        SignUpActivityState(firstName = firstname, lastName = lastname)
    }
    val lengthLimit = 24
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    suspend fun updateFirstname(name: String) {
        _firstnameFlow.emit(name)
    }

    suspend fun updateLastName(lastName: String) {
        _lastNameFlow.emit(lastName)
    }
}