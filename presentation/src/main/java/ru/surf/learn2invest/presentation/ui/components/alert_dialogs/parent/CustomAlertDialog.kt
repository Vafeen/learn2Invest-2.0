package ru.surf.learn2invest.presentation.ui.components.alert_dialogs.parent


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.surf.learn2invest.presentation.databinding.SimpleDialogBinding

/**
 * Класс для удобной реализации AlertDialogs
 */
internal abstract class CustomAlertDialog : DialogFragment() {
    abstract val dialogTag: String
    protected lateinit var binding: SimpleDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = SimpleDialogBinding.inflate(inflater)
        initListeners()
        return binding.root
    }

    abstract fun initListeners()
    fun showDialog(fragmentManager: FragmentManager) = show(fragmentManager, this.dialogTag)
}