package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.delete_profile

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.CustomAlertDialog

@AndroidEntryPoint
internal class DeleteProfileDialog : CustomAlertDialog() {
    override val dialogTag: String = "DeleteProfileDialog"
    private val viewModel by viewModels<DeleteProfileDialogViewModel>()
    override fun initListeners() {
        binding.text.text = requireContext().getString(R.string.asking_to_delete_profile)
        binding.yesExactly.setOnClickListener {
            lifecycleScope.launchMAIN {
                viewModel.deleteProfile(requireActivity() as AppCompatActivity)
                dismiss()
            }
        }
        binding.no.setOnClickListener {
            dismiss()
        }
    }
}



