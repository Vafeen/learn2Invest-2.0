package ru.surf.learn2invest.presentation.ui.components.screens.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.databinding.FragmentProfileBinding
import ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent.SimpleDialog
import ru.surf.learn2invest.domain.utils.launchMAIN
import ru.surf.learn2invest.presentation.utils.setStatusBarColor

/**
 * Фрагмент профиля в [HostActivity][ru.surf.learn2invest.ui.components.screens.host.HostActivity]
 */
@AndroidEntryPoint
internal class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel: ProfileFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        activity?.apply {
            setStatusBarColor(window, this, R.color.white, R.color.main_background_dark)
        }

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        initListeners()
        lifecycleScope.launchMAIN {
            viewModel.profileFlow.collect {
                binding.apply {
                    firstNameLastNameTV.text = it.let { pr -> "${pr.firstName}\n${pr.lastName}" }
                    confirmDealBtnSwitcher.isChecked = it.tradingPasswordHash != null
                    changeTradingPasswordBtn.isVisible = it.tradingPasswordHash != null
                    biometryBtnSwitcher.isChecked = it.biometry
                }
            }
        }
        return binding.root
    }

    private fun initListeners() {
        binding.also { fr ->
            fr.biometryBtn.isVisible = viewModel.isBiometricAvailable(activity = activity as AppCompatActivity)
            fr.deleteProfileTV.setOnClickListener {
                SimpleDialog(
                    context = requireContext(),
                    messageRes = R.string.asking_to_delete_profile,
                    positiveButtonTitleRes = R.string.yes_exactly,
                    negativeButtonTitleRes = R.string.no,
                    isCancelable = true,
                    onPositiveButtonClick = {
                        lifecycleScope.launchMAIN {
                            viewModel.deleteProfile(activity as AppCompatActivity)
                        }
                    },
                ).show()
            }

            fr.resetStatsBtn.setOnClickListener {
                SimpleDialog(
                    context = requireContext(),
                    messageRes = R.string.reset_stats,
                    positiveButtonTitleRes = R.string.yes_exactly,
                    negativeButtonTitleRes = R.string.no,
                    isCancelable = true,
                    onPositiveButtonClick = {
                        lifecycleScope.launchMAIN {
                            viewModel.resetStats(requireContext())
                        }
                    }
                ).show()
            }

            fr.biometryBtn.setOnClickListener {
                viewModel.biometryBtnSwitch(activity as AppCompatActivity)
            }

            fr.changeTradingPasswordBtn.setOnClickListener {
                viewModel.changeTradingPassword(requireContext())
            }

            fr.confirmDealBtn.setOnClickListener {
                viewModel.changeTransactionConfirmation(requireContext())
            }

            fr.changePINBtn.setOnClickListener {
                viewModel.changePIN(requireContext())
            }

        }
    }
}