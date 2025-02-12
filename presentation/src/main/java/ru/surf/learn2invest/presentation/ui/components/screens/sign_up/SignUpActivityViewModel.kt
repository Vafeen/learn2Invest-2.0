package ru.surf.learn2invest.presentation.ui.components.screens.sign_up

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.domain.services.ProfileManager
import javax.inject.Inject

/**
 * ViewModel для экрана регистрации. Обрабатывает логику обновления имени и фамилии пользователя,
 * а также валидацию введенных данных.
 */
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

    /**
     * Обновление профиля пользователя.
     * @param updating функция, которая обновляет профиль
     */
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    /**
     * Обновление имени пользователя.
     * @param name новое имя пользователя
     */
    suspend fun updateFirstname(name: String) {
        _firstnameFlow.emit(name)
    }

    /**
     * Обновление фамилии пользователя.
     * @param lastName новая фамилия пользователя
     */
    suspend fun updateLastName(lastName: String) {
        _lastNameFlow.emit(lastName)
    }
}