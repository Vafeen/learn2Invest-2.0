package ru.surf.learn2invest.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.ProfileManager
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var profileManager: ProfileManager

    override fun onCreate() {
        super.onCreate()
        with(ProcessLifecycleOwner.get()) {
            lifecycleScope.launch(Dispatchers.IO) {
                profileManager.initProfile()
            }
        }
    }
}