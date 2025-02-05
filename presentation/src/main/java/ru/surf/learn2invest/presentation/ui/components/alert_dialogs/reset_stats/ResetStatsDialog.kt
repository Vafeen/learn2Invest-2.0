package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.reset_stats

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.CustomAlertDialog

@AndroidEntryPoint
internal class ResetStatsDialog : CustomAlertDialog() {
    override val dialogTag: String = "ResetStatsDialog"
    override fun initListeners() {
        binding.text.text = requireContext().getString(R.string.reset_stats)
        binding.yesExactly.setOnClickListener {
            lifecycleScope.launchMAIN {
                viewModel.resetStats(requireContext())
                dismiss()
            }
        }
        binding.no.setOnClickListener {
            dismiss()
        }
    }

    private val viewModel by viewModels<ResetStatsDialogViewModel>()


}