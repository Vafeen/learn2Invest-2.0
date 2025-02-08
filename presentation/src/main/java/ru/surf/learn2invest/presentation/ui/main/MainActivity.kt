package ru.surf.learn2invest.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivityMainBinding
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.presentation.ui.components.screens.sign_up.SignUpActivity
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor(window, this, R.color.black, R.color.black)
        setNavigationBarColor(window, this, R.color.black, R.color.black)
        lifecycleScope.launchMAIN {
            skipSplash()
        }
    }

    /**
     * Функция показа анимированного splash-скрина и проверки, есть ли у нас зарегистрированный пользователь
     */
    private suspend fun skipSplash() {
        viewModel.initProfile()
        if (viewModel.profileFlow.value.let {
                it.firstName != "undefined" && it.lastName != "undefined" && it.hash != null
            }) {
            runAnimatedText {
                startActivity(Intent(this@MainActivity, SignInActivity::class.java).also {
                    it.action = SignINActivityActions.SignIN.action
                })
                this@MainActivity.finish()
            }
        } else {
            delay(2000)
            startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
            this@MainActivity.finish()
        }

    }

    /**
     * Функция показа анимации приветствия пользователя
     */
    private fun runAnimatedText(onEnd: () -> Unit) {
        binding.splashTextView.alpha = 0f
        binding.splashTextView.text = "${getString(R.string.hello)}, ${viewModel.profileFlow.value.firstName}!"
        viewModel.animateAlpha(
            view = binding.splashTextView,
            duration = 2000,
            onEnd = onEnd,
            values = floatArrayOf(
                0f,
                1f
            )
        )
    }
}
