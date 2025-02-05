package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.delete_profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.database.usecase.ClearAppDatabaseUseCase
import ru.surf.learn2invest.domain.utils.withContextIO
import ru.surf.learn2invest.presentation.ui.main.MainActivity
import javax.inject.Inject

@HiltViewModel
internal class DeleteProfileDialogViewModel @Inject constructor(
    private val clearAppDatabaseUseCase: ClearAppDatabaseUseCase,
) : ViewModel() {
    suspend fun deleteProfile(activity: AppCompatActivity) {
        activity.finish()
        withContextIO {
            clearAppDatabaseUseCase()
        }
        activity.startActivity(
            Intent(
                activity,
                MainActivity::class.java
            )
        )
    }
}