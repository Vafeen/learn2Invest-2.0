package ru.surf.learn2invest.presentation.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivityMainBinding
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignINActivityActions
import ru.surf.learn2invest.presentation.ui.components.screens.sign_in.SignInActivity
import ru.surf.learn2invest.presentation.ui.components.screens.sign_up.SignUpActivity
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor(window, this, R.color.black, R.color.black)
        setNavigationBarColor(window, this, R.color.black, R.color.black)

        skipSplash()
    }

    /**
     * Функция показа анимированного splash-скрина и проверки, есть ли у нас зарегистрированный пользователь
     */
    private fun skipSplash() {
        lifecycleScope.launch(Dispatchers.Main) {
            val intent =
                if (viewModel.profileFlow.value.let {
                        it.firstName != "undefined" && it.lastName != "undefined" && it.hash != null
                    }) {
                    runAnimatedText()

                    Intent(this@MainActivity, SignInActivity::class.java).also {
                        it.action = SignINActivityActions.SignIN.action
                    }
                } else {
                    Intent(this@MainActivity, SignUpActivity::class.java)
                }
            delay(2000)
            startActivity(intent)
            this@MainActivity.finish()
        }
    }

    /**
     * Функция показа анимации приветствия пользователя
     */
    private fun runAnimatedText() {
        (ContextCompat.getString(
            this, R.string.hello
        ) + ", ${viewModel.profileFlow.value.firstName}!").let {
            binding.splashTextView.text = it
        }
        binding.splashTextView.alpha = 0f
        val animator = ObjectAnimator.ofFloat(binding.splashTextView, "alpha", 0f, 1f)
        animator.duration = 2000 // Длительность анимации в миллисекундах
        animator.start()
    }
}
