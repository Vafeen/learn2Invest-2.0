package ru.surf.learn2invest.presentation.ui.components.screens.host

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivityHostBinding
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor

/**
 * Главный экран приложения с BottomBar.
 * Эта активность управляет отображением основного контента и навигацией в приложении.
 * Она также настраивает нижнюю навигационную панель (BottomBar) и связывает ее с контроллером навигации.
 */
@AndroidEntryPoint
internal class HostActivity : AppCompatActivity() {

    // Переменная для привязки элементов UI
    private lateinit var binding: ActivityHostBinding

    /**
     * Метод жизненного цикла активити, вызываемый при создании.
     * Здесь настраивается внешний вид экрана и навигация.
     *
     * @param savedInstanceState Состояние, сохраненное при предыдущем запуске.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливает цвета для навигационной панели
        setNavigationBarColor(
            window, // окно активити
            this,   // контекст активности
            R.color.accent_background, // основной цвет фона
            R.color.accent_background_dark // цвет фона для темной темы
        )

        // Привязка интерфейса с использованием ActivityHostBinding
        binding = ActivityHostBinding.inflate(layoutInflater)

        // Устанавливаем разметку для активности
        setContentView(binding.root)

        // Находим контейнер для фрагментов и получаем контроллер навигации
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.host_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        // Настройка нижней навигационной панели (Bottom Navigation) для работы с контроллером навигации
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}
