package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.delete_profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.database.usecase.ClearAppDatabaseUseCase
import ru.surf.learn2invest.domain.utils.withContextIO
import ru.surf.learn2invest.presentation.ui.main.MainActivity
import javax.inject.Inject

/**
 * ViewModel для обработки удаления профиля пользователя.
 *
 * @property clearAppDatabaseUseCase UseCase для очистки базы данных приложения.
 */
@HiltViewModel
internal class DeleteProfileDialogViewModel @Inject constructor(
    private val clearAppDatabaseUseCase: ClearAppDatabaseUseCase,
) : ViewModel() {

    /**
     * Удаляет профиль пользователя, очищает базу данных и перезапускает приложение.
     *
     * @param activity Активность, из которой вызывается удаление профиля.
     */
    suspend fun deleteProfile(activity: AppCompatActivity) {
        // Завершает текущую активность.
        activity.finish()

        // Очищает базу данных в фоновом потоке.
        withContextIO {
            clearAppDatabaseUseCase()
        }

        // Перезапускает приложение, переходя на главный экран.
        activity.startActivity(
            Intent(activity, MainActivity::class.java)
        )
    }
}