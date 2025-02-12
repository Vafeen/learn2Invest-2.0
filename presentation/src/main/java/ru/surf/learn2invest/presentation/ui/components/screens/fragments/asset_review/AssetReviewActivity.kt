package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_review

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivityAssetReviewBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog.BuyDialog
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.sell_dialog.SellDialog
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview.AssetOverviewFragment
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.subhistory.SubHistoryFragment
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor

/**
 * Экран обзора актива, позволяющий пользователю просматривать подробности актива,
 * его историю и совершать действия с активом (покупка/продажа).
 */
@AndroidEntryPoint
internal class AssetReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssetReviewBinding
    private val viewModel: AssetReviewActivityViewModel by viewModels()
    private var isOverviewSelected = true

    /**
     * Инициализация экрана и привязка компонентов UI.
     * Устанавливаются цвета для статус бара и навигационной панели, инициализируются кнопки
     * и фрагменты для отображения.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Установка цветов для статус бара и навигационной панели
        setStatusBarColor(window, this, R.color.white, R.color.main_background_dark)
        setNavigationBarColor(window, this, R.color.white, R.color.main_background_dark)

        // Инициализация привязки и привязка UI
        binding = ActivityAssetReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение данных о символе, ID и названии актива из intent
        val id = intent.getStringExtra(AssetConstants.ID.key) ?: ""
        val name = intent.getStringExtra(AssetConstants.NAME.key) ?: ""
        val symbol = intent.getStringExtra(AssetConstants.SYMBOL.key) ?: ""

        // Обработчик кнопки возврата
        binding.goBack.setOnClickListener {
            finish()
        }

        // По умолчанию отображается фрагмент с обзором актива
        goToFragment(AssetOverviewFragment.newInstance(id, symbol))

        // Обновление цвета кнопок в зависимости от текущего состояния
        updateButtonColors()

        // Обработчик клика на кнопку "Обзор"
        binding.assetReviewBtn.setOnClickListener {
            if (!isOverviewSelected) {
                isOverviewSelected = true
                updateButtonColors()
                goToFragment(AssetOverviewFragment.newInstance(id, symbol))
            }
        }

        // Обработчик клика на кнопку "История"
        binding.assetHistoryBtn.setOnClickListener {
            if (isOverviewSelected) {
                isOverviewSelected = false
                updateButtonColors()
                goToFragment(SubHistoryFragment.newInstance(symbol))
            }
        }

        // Установка названия и символа актива в UI
        binding.coinName.text = name
        binding.coinSymbol.text = symbol

        // Загрузка и отображение иконки актива
        viewModel.loadImage(binding.coinIcon, symbol)

        // Обработчик кнопки "Купить актив"
        binding.buyAssetBtn.setOnClickListener {
            BuyDialog.newInstance(id, name, symbol).showDialog(supportFragmentManager)
        }

        // Обработчик кнопки "Продать актив"
        binding.sellAssetBtn.setOnClickListener {
            SellDialog.newInstance(id, name, symbol).showDialog(supportFragmentManager)
        }
    }

    /**
     * Обновление цветов кнопок в зависимости от текущего состояния отображаемого фрагмента.
     */
    private fun updateButtonColors() {
        // Определение текущей темы (темная или светлая)
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        val accentColor =
            getColor(if (isDarkTheme) R.color.accent_background_dark else R.color.accent_background)
        val defaultColor =
            getColor(if (isDarkTheme) R.color.accent_button_dark else R.color.view_background)

        // Установка цвета фона для кнопок "Обзор" и "История"
        binding.assetReviewBtn.backgroundTintList = ColorStateList.valueOf(
            if (isOverviewSelected) accentColor else defaultColor
        )

        binding.assetHistoryBtn.backgroundTintList = ColorStateList.valueOf(
            if (isOverviewSelected) defaultColor else accentColor
        )
    }

    /**
     * Отмена загрузки иконки при уничтожении активности.
     */
    override fun onDestroy() {
        viewModel.cancelLoadingImage()
        super.onDestroy()
    }

    /**
     * Переход на новый фрагмент.
     */
    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}