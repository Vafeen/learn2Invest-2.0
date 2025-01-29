package ru.surf.learn2invest.presentation.ui.components.screens.fragments.marketreview

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.FragmentMarketReviewBinding
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import javax.inject.Inject


/**
 * Фрагмент обзора рынка в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */
@AndroidEntryPoint
class MarketReviewFragment : Fragment() {
    private val binding by lazy { FragmentMarketReviewBinding.inflate(layoutInflater) }
    private val viewModel: MarketReviewFragmentViewModel by viewModels()

    @Inject
    lateinit var adapter: MarketReviewAdapter
    private lateinit var realTimeUpdateJob: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.apply {
            setStatusBarColor(window, this, R.color.white, R.color.main_background_dark)
        }

        binding.marketReviewRecyclerview.layoutManager = LinearLayoutManager(this.requireContext())
        binding.marketReviewRecyclerview.adapter = adapter

        lifecycleScope.launch {
            viewModel.filterOrder.collect {
                binding.apply {
                    if (it) {
                        filterByPrice.setIconResource(R.drawable.arrow_top_green)
                        filterByPrice.setIconTintResource(R.color.label)
                    } else {
                        filterByPrice.setIconResource(R.drawable.arrow_bottom_red)
                        filterByPrice.setIconTintResource(R.color.recession)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.apply {
                    marketReviewRecyclerview.isVisible = it.not()
                    binding.progressBar.isVisible = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isError.collect {
                binding.apply {
                    marketReviewRecyclerview.isVisible = it.not()
                    networkErrorTv.isVisible = it
                    networkErrorIv.isVisible = it
                }
            }
        }

        lifecycleScope.launch {
            viewModel.searchedData.collect {
                if (adapter.data.size != it.size) {
                    adapter.data = it
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.data = it
                    if (viewModel.isRealtimeUpdate) {
                        adapter.notifyItemRangeChanged(
                            viewModel.firstUpdateElement,
                            viewModel.amountUpdateElement
                        )
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.data.collect {
                if (it.isNotEmpty()) {
                    adapter.data = it
                    if (viewModel.isRealtimeUpdate) {
                        adapter.notifyItemRangeChanged(
                            viewModel.firstUpdateElement,
                            viewModel.amountUpdateElement
                        )
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                    binding.searchEditText.setAdapter(
                        ArrayAdapter(this@MarketReviewFragment.requireContext(),
                            android.R.layout.simple_expandable_list_item_1,
                            it.map { element -> element.name })
                    )
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filterState.collect {
                binding.apply {
                    val isDarkTheme =
                        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

                    filterByMarketcap.backgroundTintList =
                        ColorStateList.valueOf(
                            resources.getColor(
                                if (it[FILTER_BY_MARKETCAP] == true) {
                                    if (isDarkTheme)
                                        R.color.accent_background_dark
                                    else
                                        R.color.accent_background
                                } else {
                                    if (isDarkTheme)
                                        R.color.accent_button_dark
                                    else
                                        R.color.view_background
                                }
                            )
                        )
                    filterByChangePercent24Hr.backgroundTintList =
                        ColorStateList.valueOf(
                            resources.getColor(
                                if (it[FILTER_BY_PERCENT] == true) {
                                    if (isDarkTheme)
                                        R.color.accent_background_dark
                                    else
                                        R.color.accent_background
                                } else {
                                    if (isDarkTheme)
                                        R.color.accent_button_dark
                                    else
                                        R.color.view_background
                                }
                            )
                        )
                    filterByPrice.backgroundTintList =
                        ColorStateList.valueOf(
                            resources.getColor(
                                if (it[FILTER_BY_PRICE] == true) {
                                    if (isDarkTheme)
                                        R.color.accent_background_dark
                                    else
                                        R.color.accent_background
                                } else {
                                    if (isDarkTheme)
                                        R.color.accent_button_dark
                                    else
                                        R.color.view_background
                                }
                            )
                        )
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isSearch.collect {
                binding.apply {
                    youSearch.isVisible = it
                    clearTv.isVisible = it
                    cancelTV.isVisible = it
                    filterByPrice.isVisible = it.not()
                    filterByMarketcap.isVisible = it.not()
                    filterByChangePercent24Hr.isVisible = it.not()
                    searchEditText.text.clear()
                    if (it) searchEditText.hint = ""
                    if (it) {
                        adapter.data = viewModel.searchedData.value
                    } else adapter.data = viewModel.data.value
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.apply {
            filterByMarketcap.setOnClickListener {
                marketReviewRecyclerview.layoutManager?.scrollToPosition(0)
                viewModel.filterByMarketcap()
            }

            filterByChangePercent24Hr.setOnClickListener {
                marketReviewRecyclerview.layoutManager?.scrollToPosition(0)
                viewModel.filterByPercent()
            }

            filterByPrice.setOnClickListener {
                marketReviewRecyclerview.layoutManager?.scrollToPosition(0)
                viewModel.filterByPrice()
            }

            textInputLayout.setEndIconOnClickListener {
                searchEditText.text.clear()
            }

            searchEditText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) viewModel.setSearchState(true)
            }

            searchEditText.setOnItemClickListener { parent, view, position, id ->
                viewModel.setSearchState(true, searchEditText.text.toString())
            }

            clearTv.setOnClickListener {
                viewModel.clearSearchData()
            }

            cancelTV.setOnClickListener {
                viewModel.setSearchState(false)
                hideKeyboardFrom(requireContext(), searchEditText)
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        realTimeUpdateJob = startRealtimeUpdate()
    }

    override fun onPause() {
        super.onPause()
        realTimeUpdateJob.cancel()
    }

    private fun startRealtimeUpdate() = lifecycleScope.launch {
        while (true) {
            delay(5000)
            val firstElement =
                (binding.marketReviewRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val lastElement =
                (binding.marketReviewRecyclerview.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            viewModel.updateData(firstElement, lastElement)
        }
    }


    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    companion object {
        const val FILTER_BY_MARKETCAP = 0
        const val FILTER_BY_PERCENT = 1
        const val FILTER_BY_PRICE = 2
    }
}