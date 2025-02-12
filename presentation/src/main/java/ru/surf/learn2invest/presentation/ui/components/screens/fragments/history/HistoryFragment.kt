package ru.surf.learn2invest.presentation.ui.components.screens.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.domain.utils.withContextMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.FragmentHistoryBinding
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.common.BaseResFragment
import ru.surf.learn2invest.presentation.utils.setStatusBarColor
import javax.inject.Inject

/**
 * Фрагмент, отображающий историю сделок. Является частью экрана [ru.surf.learn2invest.presentation.ui.components.screens.host.HostActivity].
 * В данном фрагменте отображается список транзакций пользователя.
 */
@AndroidEntryPoint
internal class HistoryFragment : BaseResFragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryFragmentViewModel by viewModels()

    @Inject
    lateinit var adapter: HistoryFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.apply {
            setStatusBarColor(
                window,
                this,
                R.color.accent_background,
                R.color.accent_background_dark
            )
        }

        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerview.adapter = adapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchIO {
            viewModel.data.collect {
                withContextMAIN {
                    if (it.isEmpty()) {
                        binding.historyRecyclerview.isVisible = false
                        binding.noActionsTv.isVisible = true
                    } else {
                        adapter.data = it
                    }
                }
            }
        }
    }
}
