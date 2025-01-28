package ru.surf.learn2invest.ui.components.screens.fragments.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.domain.database.usecase.GetAllTransactionUseCase
import ru.surf.learn2invest.domain.domain_models.Transaction

import javax.inject.Inject

@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(getAllTransactionUseCase: GetAllTransactionUseCase) :
    ViewModel() {
    val data: Flow<List<Transaction>> =
        getAllTransactionUseCase().map { it.reversed() }
}