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
 * Экран обзора актива
 */
@AndroidEntryPoint
internal class AssetReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssetReviewBinding
    private val viewModel: AssetReviewActivityViewModel by viewModels()
    private var isOverviewSelected = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStatusBarColor(window, this, R.color.white, R.color.main_background_dark)
        setNavigationBarColor(window, this, R.color.white, R.color.main_background_dark)

        binding = ActivityAssetReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(AssetConstants.ID.key) ?: ""
        val name = intent.getStringExtra(AssetConstants.NAME.key) ?: ""
        val symbol = intent.getStringExtra(AssetConstants.SYMBOL.key) ?: ""

        binding.goBack.setOnClickListener {
            finish()
        }

        goToFragment(AssetOverviewFragment.newInstance(id, symbol))

        updateButtonColors()

        binding.assetReviewBtn.setOnClickListener {
            if (!isOverviewSelected) {
                isOverviewSelected = true
                updateButtonColors()
                goToFragment(AssetOverviewFragment.newInstance(id, symbol))
            }
        }

        binding.assetHistoryBtn.setOnClickListener {
            if (isOverviewSelected) {
                isOverviewSelected = false
                updateButtonColors()
                goToFragment(SubHistoryFragment.newInstance(symbol))
            }
        }

        binding.coinName.text = name
        binding.coinSymbol.text = symbol
        viewModel.loadImage(binding.coinIcon, symbol)
        binding.buyAssetBtn.setOnClickListener {
            BuyDialog.newInstance(id, name, symbol).showDialog(supportFragmentManager)
        }

        binding.sellAssetBtn.setOnClickListener {
            SellDialog.newInstance(id, name, symbol).showDialog(supportFragmentManager)
        }
    }

    private fun updateButtonColors() {
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        val accentColor =
            getColor(if (isDarkTheme) R.color.accent_background_dark else R.color.accent_background)
        val defaultColor =
            getColor(if (isDarkTheme) R.color.accent_button_dark else R.color.view_background)

        binding.assetReviewBtn.backgroundTintList = ColorStateList.valueOf(
            if (isOverviewSelected) accentColor else defaultColor
        )

        binding.assetHistoryBtn.backgroundTintList = ColorStateList.valueOf(
            if (isOverviewSelected) defaultColor else accentColor
        )
    }

    override fun onDestroy() {
        viewModel.cancelLoadingImage()
        super.onDestroy()
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}