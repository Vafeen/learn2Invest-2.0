package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.reset_stats

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.database.usecase.ClearAppDatabaseUseCase
import ru.surf.learn2invest.domain.services.ProfileManager
import ru.surf.learn2invest.domain.utils.withContextIO
import ru.surf.learn2invest.presentation.R
import javax.inject.Inject

@HiltViewModel
internal class ResetStatsDialogViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val clearAppDatabaseUseCase: ClearAppDatabaseUseCase,
) : ViewModel() {

    suspend fun resetStats(context: Context) {
        val savedProfile = profileManager.profileFlow.value.copy(
            fiatBalance = 0f,
            assetBalance = 0f
        )
        withContextIO {
            clearAppDatabaseUseCase()
            profileManager.updateProfile {
                savedProfile
            }
        }
        Toast.makeText(context, context.getString(R.string.stat_reset), Toast.LENGTH_LONG).show()
    }
}