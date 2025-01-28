package ru.surf.learn2invest.ui.components.screens.fragments.subhistory

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.surf.learn2invest.domain.database.usecase.GetFilteredBySymbolTransactionUseCase
import ru.surf.learn2invest.domain.domain_models.Transaction


class SubHistoryFragmentViewModel @AssistedInject constructor(
    getFilteredBySymbolTransactionUseCase: GetFilteredBySymbolTransactionUseCase,
    @Assisted val symbol: String
) : ViewModel() {

    var data: Flow<List<Transaction>> =
        getFilteredBySymbolTransactionUseCase(symbol).map { it.reversed() }

    @AssistedFactory
    interface Factory {
        fun createSubHistoryAssetViewModel(symbol: String): SubHistoryFragmentViewModel
    }
}