package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.presentation.databinding.FragmentAssetOverviewBinding
import ru.surf.learn2invest.presentation.ui.components.chart.Last7DaysFormatter
import ru.surf.learn2invest.presentation.ui.components.chart.LineChartHelper
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.utils.viewModelCreator
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Фрагмент обзора актива в [AssetReviewActivity][ru.surf.learn2invest.ui.components.screens.fragments.asset_review.AssetReviewActivity]
 */
@AndroidEntryPoint
internal class AssetOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAssetOverviewBinding

    @Inject
    lateinit var factory: AssetOverViewFragmentViewModel.Factory

    private val viewModel: AssetOverViewFragmentViewModel by viewModelCreator {
        factory.createAssetOverViewFragmentViewModel(
            id = requireArguments().getString(AssetConstants.ID.key) ?: ""
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetOverviewBinding.inflate(inflater, container, false)
        val dateFormatterStrategy = Last7DaysFormatter()
        viewModel.chartHelper = LineChartHelper(requireContext(), dateFormatterStrategy)
        viewModel.chartHelper.setupChart(binding.chart)
        viewModel.loadChartData()
        lifecycleScope.launchMAIN {
            viewModel.formattedMarketCapFlow.collect {
                binding.capitalisation.text = NumberFormat.getInstance(Locale.US).apply {
                    maximumFractionDigits = 0
                }.format(it).getWithCurrency()
            }
        }
        lifecycleScope.launchMAIN {
            viewModel.formattedPriceFlow.collect {
                binding.price.text = String.format(Locale.US, "%.8f", it).getWithCurrency()
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.startRealTimeUpdate()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRealTimeUpdateJob()
    }

    companion object {
        fun newInstance(id: String): AssetOverviewFragment {
            val fragment = AssetOverviewFragment()
            val args = Bundle()
            args.putString(AssetConstants.ID.key, id)
            fragment.arguments = args
            return fragment
        }
    }
}