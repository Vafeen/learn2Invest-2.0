package ru.surf.learn2invest.presentation.ui.components.screens.fragments.portfolio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.FragmentPortfolioBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.refill_account_dialog.RefillAccountDialog
import ru.surf.learn2invest.presentation.ui.components.chart.AssetBalanceHistoryFormatter
import ru.surf.learn2invest.presentation.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.common.BaseResFragment
import ru.surf.learn2invest.presentation.utils.DevStrLink
import ru.surf.learn2invest.presentation.utils.getVersionName
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import java.util.Locale
import javax.inject.Inject

/**
 * Фрагмент портфеля в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */
@AndroidEntryPoint
internal class PortfolioFragment : BaseResFragment() {
    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var chartHelper: LineChartHelper
    private val viewModel: PortfolioFragmentViewModel by viewModels()

    @Inject
    lateinit var adapter: PortfolioAdapter

    // Создание представления фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        // Настройка цвета статус-бара
        activity?.apply {
            setStatusBarColor(
                window,
                this,
                R.color.accent_background,
                R.color.accent_background_dark
            )
        }

        // Инициализация привязки для фрагмента
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        // Настройка RecyclerView для отображения активов
        setupAssetsRecyclerView()

        // Подписка на изменение общего баланса
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.totalBalance.collect { balance ->
                binding.balanceText.text = balance.getWithCurrency()
                val isBalanceNonZero = balance != 0f
                binding.chart.isVisible = isBalanceNonZero
                binding.percent.isVisible = isBalanceNonZero
            }
        }

        // Подписка на изменение баланса счета в фиатной валюте
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.fiatBalance.collect { balance ->
                binding.accountFunds.text = balance.getWithCurrency()
            }
        }

        // Настройка графика с историей баланса активов
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val dates = viewModel.getAssetBalanceHistoryDates()
            val dateFormatterStrategy = AssetBalanceHistoryFormatter(dates)
            chartHelper = LineChartHelper(requireContext(), dateFormatterStrategy)
            chartHelper.setupChart(binding.chart)
            viewModel.chartData.collect { data ->
                chartHelper.updateData(data)
            }
        }

        // Кнопка пополнения счета
        binding.topUpBtn.setOnClickListener {
            RefillAccountDialog().showDialog(parentFragmentManager)
        }

        // Подписка на изменения активов и обновление списка
        viewLifecycleOwner.lifecycleScope.launchMAIN {
            viewModel.assetsFlow.collect { assets ->
                adapter.assets = assets
                binding.assets.isVisible = assets.isNotEmpty()
                binding.assetsAreEmpty.isVisible = assets.isEmpty()
            }
        }

        // Подписка на изменения цен активов и обновление данных адаптера
        viewLifecycleOwner.lifecycleScope.launchMAIN {
            viewModel.priceChanges.collect { priceChanges ->
                adapter.priceChanges = priceChanges
            }
        }

        // Подписка на изменение процента изменения портфеля
        viewLifecycleOwner.lifecycleScope.launchMAIN {
            viewModel.portfolioChangePercentage.collect { percentage ->
                binding.percent.apply {
                    // Форматирование и установка текста процента
                    text = if (percentage == 0f) "%.2f%%".format(Locale.getDefault(), percentage)
                    else "%+.2f%%".format(Locale.getDefault(), percentage)

                    // Установка фона в зависимости от изменения
                    background = when {
                        percentage > 0 -> getDrawableRes(R.drawable.percent_increase_background)

                        percentage < 0 -> getDrawableRes(R.drawable.percent_recession_background)

                        else -> getDrawableRes(R.drawable.percent_zero_background)
                    }
                }
            }
        }

        // Инициализация слушателей для бокового меню
        initDrawerListeners()

        // Открытие бокового меню по нажатию на кнопку
        binding.imageButton.setOnClickListener {
            openDrawer()
        }

        // Закрытие бокового меню при прикосновении к экрану
        binding.drawerLayout.setOnTouchListener { _, _ ->
            closeDrawer()
            false
        }

        return binding.root
    }

    // Метод для старта обновления цен при возобновлении видимости фрагмента
    override fun onResume() {
        super.onResume()
        viewModel.startUpdatingPriceFLow()
    }

    // Метод для остановки обновления цен при скрытии фрагмента
    override fun onStop() {
        super.onStop()
        viewModel.stopUpdatingPriceFlow()
        closeDrawer()
    }

    // Настройка RecyclerView для отображения активов
    private fun setupAssetsRecyclerView() {
        binding.assets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.assets.adapter = adapter
    }

    // Открытие бокового меню
    private fun openDrawer() {
        activity?.apply {
            val drawer = binding.drawerLayout

            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START)
            }
        }
    }

    // Закрытие бокового меню
    private fun closeDrawer() {
        activity?.apply {
            val drawer = binding.drawerLayout

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
        }
    }

    // Инициализация слушателей для бокового меню
    private fun initDrawerListeners() {
        binding.apply {

            // Кнопка "Написать нам"
            contactUs.setOnClickListener {
                startActivity(Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto: ${DevStrLink.CHERY}")
                })
            }

            // Кнопка для перехода к исходному коду
            code.setOnClickListener {
                openLink(DevStrLink.CODE)
            }

            // Кнопка для перехода к макету в Figma
            figma.setOnClickListener {
                openLink(DevStrLink.FIGMA)
            }

            // Отображение версии приложения
            versionCode.text = requireContext().getVersionName()
        }
    }

    // Открытие внешней ссылки
    private fun openLink(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}
