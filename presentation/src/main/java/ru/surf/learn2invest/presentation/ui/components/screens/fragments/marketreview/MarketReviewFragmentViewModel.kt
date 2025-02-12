package ru.surf.learn2invest.presentation.ui.components.screens.fragments.marketreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.surf.learn2invest.domain.database.usecase.ClearSearchedCoinUseCase
import ru.surf.learn2invest.domain.database.usecase.GetAllSearchedCoinUseCase
import ru.surf.learn2invest.domain.database.usecase.InsertSearchedCoinUseCase
import ru.surf.learn2invest.domain.domain_models.CoinReview
import ru.surf.learn2invest.domain.domain_models.SearchedCoin
import ru.surf.learn2invest.domain.network.ResponseResult
import ru.surf.learn2invest.domain.network.usecase.GetCoinReviewUseCase
import ru.surf.learn2invest.domain.network.usecase.GetMarketReviewUseCase
import ru.surf.learn2invest.domain.toCoinReview
import ru.surf.learn2invest.domain.utils.launchIO
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.marketreview.MarketReviewFragment.Companion.FILTER_BY_MARKETCAP
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.marketreview.MarketReviewFragment.Companion.FILTER_BY_PERCENT
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.marketreview.MarketReviewFragment.Companion.FILTER_BY_PRICE
import javax.inject.Inject

/**
 * ViewModel для экрана MarketReview, который управляет состоянием данных для отображения
 * информации о монетах и их фильтрации.
 *
 * @property getMarkerReviewUseCase Используется для получения списка всех рыночных обзоров.
 * @property insertSearchedCoinUseCase Используется для добавления монет в список поисковых запросов.
 * @property getAllSearchedCoinUseCase Используется для получения всех ранее добавленных монет в поисковых запросах.
 * @property getCoinReviewUseCase Используется для получения подробной информации о конкретной монете.
 * @property clearSearchedCoinUseCase Используется для очистки списка поисковых запросов.
 */
@HiltViewModel
internal class MarketReviewFragmentViewModel @Inject constructor(
    private val getMarkerReviewUseCase: GetMarketReviewUseCase,
    private val insertSearchedCoinUseCase: InsertSearchedCoinUseCase,
    private val getAllSearchedCoinUseCase: GetAllSearchedCoinUseCase,
    private val getCoinReviewUseCase: GetCoinReviewUseCase,
    private val clearSearchedCoinUseCase: ClearSearchedCoinUseCase,
) : ViewModel() {

    /**
     * Состояние, хранящее список монет с их рыночными обзорами.
     */
    private var _data: MutableStateFlow<List<CoinReview>> = MutableStateFlow(listOf())
    val data: StateFlow<List<CoinReview>> get() = _data.asStateFlow()

    /**
     * Состояние загрузки данных. Показывает, идет ли процесс загрузки.
     */
    private var _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    /**
     * Состояние ошибки. Указывает, возникла ли ошибка при загрузке данных.
     */
    private var _isError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isError: StateFlow<Boolean> get() = _isError

    /**
     * Состояние фильтров для сортировки монет по различным критериям.
     */
    private var _filterState: MutableStateFlow<Map<Int, Boolean>> = MutableStateFlow(
        mapOf(
            Pair(FILTER_BY_MARKETCAP, true),
            Pair(FILTER_BY_PERCENT, false),
            Pair(FILTER_BY_PRICE, false)
        )
    )
    val filterState: StateFlow<Map<Int, Boolean>> get() = _filterState

    /**
     * Состояние, указывающее на порядок сортировки (по возрастанию или убыванию).
     */
    private val _filterOrder: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val filterOrder: StateFlow<Boolean> get() = _filterOrder

    /**
     * Флаг, указывающий, был ли уже применен фильтр по цене.
     */
    private var firstTimePriceFilter = true

    /**
     * Состояние, указывающее, был ли выполнен поиск.
     */
    private var _isSearch: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearch: StateFlow<Boolean> get() = _isSearch

    /**
     * Состояние, содержащее данные, полученные по результатам поиска.
     */
    private var _searchedData: MutableStateFlow<List<CoinReview>> = MutableStateFlow(listOf())
    val searchedData: StateFlow<List<CoinReview>> get() = _searchedData

    /**
     * Индекс первого элемента, который должен быть обновлен при загрузке новых данных.
     */
    private var firstUpdateElement = 0

    /**
     * Количество элементов, которые должны быть обновлены.
     */
    private var amountUpdateElement = 0

    /**
     * Инициализация данных, выполняется при создании ViewModel.
     * Получает рыночные обзоры и обновляет состояние данных.
     */
    init {
        initData()
    }

    /**
     * Инициализирует данные, выполняет загрузку рыночных обзоров.
     */
    private fun initData() {
        viewModelScope.launchIO {
            when (val result = getMarkerReviewUseCase()) {
                is ResponseResult.Success -> {
                    _isLoading.value = false
                    _isError.value = false
                    _data.value = result.value.toMutableList().filter {
                        it.marketCapUsd > 0f && it.priceUsd > 0.1f
                    }.sortedByDescending { it.marketCapUsd }
                }

                is ResponseResult.NetworkError -> {
                    _isLoading.value = false
                    _isError.value = true
                }
            }
        }
    }

    /**
     * Активирует состояние фильтра для выбранного элемента.
     *
     * @param element Идентификатор фильтра.
     */
    private fun activateFilterState(element: Int) {
        if (element != FILTER_BY_PRICE) firstTimePriceFilter = true
        _filterState.update {
            it.mapValues { a -> a.key == element }
        }
    }

    /**
     * Сортирует данные по рыночной капитализации.
     */
    fun filterByMarketcap() {
        activateFilterState(FILTER_BY_MARKETCAP)
        _data.update {
            it.sortedByDescending { element -> element.marketCapUsd }.toMutableList()
        }
    }

    /**
     * Сортирует данные по процентному изменению за 24 часа.
     */
    fun filterByPercent() {
        activateFilterState(FILTER_BY_PERCENT)
        _data.update {
            it.sortedByDescending { element -> element.changePercent24Hr }.toMutableList()
        }
    }

    /**
     * Сортирует данные по цене.
     */
    fun filterByPrice() {
        activateFilterState(FILTER_BY_PRICE)
        if (!firstTimePriceFilter) {
            _filterOrder.update {
                it.not()
            }
        } else firstTimePriceFilter = false
        _data.update {
            if (filterOrder.value) it.sortedBy { element -> element.priceUsd }
                .toMutableList()
            else it.sortedByDescending { element -> element.priceUsd }.toMutableList()
        }
    }

    /**
     * Устанавливает состояние поиска и обновляет данные с учетом поискового запроса.
     *
     * @param state Состояние поиска.
     * @param searchRequest Строка поискового запроса.
     */
    fun setSearchState(state: Boolean, searchRequest: String = "") {
        var tempSearch = listOf<String>()
        _isSearch.update {
            state
        }
        if (state) {
            viewModelScope.launchIO {
                if (searchRequest.isNotBlank()) {
                    insertSearchedCoinUseCase(SearchedCoin(coinID = searchRequest))
                }
                tempSearch = getAllSearchedCoinUseCase().first().map { it.coinID }
                _searchedData.update {
                    _data.value.filter { element -> tempSearch.contains(element.name) }.reversed()
                }
            }
        }
    }

    /**
     * Обновляет данные для отображения в указанном диапазоне элементов.
     *
     * @param firstElement Индекс первого элемента.
     * @param lastElement Индекс последнего элемента.
     */
    fun updateData(firstElement: Int, lastElement: Int) {
        val tempUpdate = mutableListOf<CoinReview>()
        val updateDestinationLink = if (_isSearch.value) _searchedData else _data
        if (updateDestinationLink.value.isNotEmpty()
            && firstElement != NO_POSITION
            && updateDestinationLink.value.size > lastElement
        ) {
            firstUpdateElement = firstElement
            amountUpdateElement = lastElement - firstElement + 1
            viewModelScope.launch(Dispatchers.IO) {
                for (index in firstElement..lastElement) {
                    when (val result =
                        getCoinReviewUseCase.invoke(updateDestinationLink.value[index].id)) {
                        is ResponseResult.Success -> {
                            tempUpdate.add(result.value.toCoinReview())
                        }

                        is ResponseResult.NetworkError -> _isError.value = true
                    }
                }
                val tempUpdateId = tempUpdate.map { it.id }
                updateDestinationLink.update {
                    it.mapNotNull { element ->
                        if (tempUpdateId.contains(element.id)) tempUpdate.find { updateElement ->
                            updateElement.id == element.id
                        }
                        else element
                    }
                }
            }
        } else initData()
    }

    /**
     * Очищает данные поиска.
     */
    fun clearSearchData() {
        viewModelScope.launchIO {
            clearSearchedCoinUseCase()
            _searchedData.update {
                listOf()
            }
        }
    }
}
