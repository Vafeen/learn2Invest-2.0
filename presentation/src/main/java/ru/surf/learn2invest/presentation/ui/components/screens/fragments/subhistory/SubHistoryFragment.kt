package ru.surf.learn2invest.presentation.ui.components.screens.fragments.subhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.presentation.databinding.FragmentAssetHistoryBinding
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.viewModelCreator
import javax.inject.Inject

/**
 * Фрагмент для отображения истории сделок с конкретной монетой в активе.
 */
@AndroidEntryPoint
internal class SubHistoryFragment : Fragment() {
    private lateinit var binding: FragmentAssetHistoryBinding

    @Inject
    lateinit var factory: SubHistoryFragmentViewModel.Factory

    // Создание ViewModel с передачей символа монеты для фильтрации данных
    private val viewModel: SubHistoryFragmentViewModel by viewModelCreator {
        factory.createSubHistoryAssetViewModel(
            symbol = requireArguments().getString(AssetConstants.SYMBOL.key) ?: ""
        )
    }

    @Inject
    lateinit var adapter: SubHistoryAdapter

    /**
     * Инфлейт и настройка layout для фрагмента.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAssetHistoryBinding.inflate(inflater, container, false)

        binding.assetHistory.layoutManager = LinearLayoutManager(this.requireContext())
        binding.assetHistory.adapter = adapter
        return binding.root
    }

    /**
     * Набор данных, который отображается в адаптере.
     */
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.data.collect {
                // Отображение данных или сообщение об отсутствии сделок
                if (it.isEmpty()) {
                    binding.assetHistory.isVisible = false
                    binding.noActionsError.isVisible = true
                } else {
                    adapter.data = it
                }
            }
        }
    }

    /**
     * Создание экземпляра фрагмента с передачей символа монеты.
     */
    companion object {
        fun newInstance(symbol: String): SubHistoryFragment {
            val fragment = SubHistoryFragment()
            val args = Bundle()
            args.putString(AssetConstants.SYMBOL.key, symbol)
            fragment.arguments = args
            return fragment
        }
    }
}
