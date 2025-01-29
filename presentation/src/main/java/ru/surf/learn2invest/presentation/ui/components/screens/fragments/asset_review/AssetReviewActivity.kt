package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_review

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.Disposable
import coil.request.ImageRequest
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.domain.network.RetrofitLinks.API_ICON
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.ActivityAssetReviewBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog.BuyDialog
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.buy_dialog.BuyDialogViewModel
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.sell_dialog.SellDialog
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview.AssetOverviewFragment
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.subhistory.SubHistoryFragment
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.setNavigationBarColor
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import ru.surf.learn2invest.presentation.utils.viewModelCreator
import javax.inject.Inject

/**
 * Экран обзора актива
 */
@AndroidEntryPoint
class AssetReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssetReviewBinding
    private lateinit var disposable: Disposable
    private var isOverviewSelected = true

    @Inject
    lateinit var buyFactory: BuyDialogViewModel.BuyFactory

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

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AssetOverviewFragment.newInstance(id))
            .commit()

        updateButtonColors()

        binding.assetReviewBtn.setOnClickListener {
            isOverviewSelected = true
            updateButtonColors()
            goToFragment(AssetOverviewFragment.newInstance(id))
        }

        binding.assetHistoryBtn.setOnClickListener {
            isOverviewSelected = false
            updateButtonColors()
            goToFragment(SubHistoryFragment.newInstance(symbol))
        }

        binding.coinName.text = name
        binding.coinSymbol.text = symbol

        val imageLoader = ImageLoader.Builder(binding.coinIcon.context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
        val request = ImageRequest.Builder(binding.coinIcon.context)
            .data("${API_ICON}${symbol.lowercase()}.svg")
            .target(onSuccess = {
                binding.coinIcon.setImageDrawable(it)
            },
                onError = {
                    binding.coinIcon.setImageResource(R.drawable.coin_placeholder)
                },
                onStart = {
                    binding.coinIcon.setImageResource(R.drawable.placeholder)
                })
            .build()
        disposable = imageLoader.enqueue(request)

        binding.buyAssetBtn.setOnClickListener {
            BuyDialog(this, viewModelCreator {
                buyFactory.createViewModel(id, name, symbol)
            }.value).also {
                it.show(supportFragmentManager, it.tag)
            }
        }

        binding.sellAssetBtn.setOnClickListener {
            SellDialog(
                dialogContext = this,
                lifecycleScope = lifecycleScope,
                id = id,
                name = name,
                symbol = symbol
            ).also {
                it.show(supportFragmentManager, it.dialogTag)
            }
        }
    }

    private fun updateButtonColors() {
        val isDarkTheme =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        val accentColor = ContextCompat.getColor(
            this,
            if (isDarkTheme) R.color.accent_background_dark else R.color.accent_background
        )
        val defaultColor = ContextCompat.getColor(
            this,
            if (isDarkTheme) R.color.accent_button_dark else R.color.view_background
        )

        binding.assetReviewBtn.backgroundTintList = ColorStateList.valueOf(
            if (isOverviewSelected) accentColor else defaultColor
        )

        binding.assetHistoryBtn.backgroundTintList = ColorStateList.valueOf(
            if (isOverviewSelected) defaultColor else accentColor
        )
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun goToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }
}