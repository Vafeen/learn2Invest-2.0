package ru.surf.learn2invest.presentation.utils

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Фабрика для создания экземпляров ViewModel с использованием пользовательского создателя.
 *
 * @param VM Тип ViewModel.
 * @property viewModelCreator Функция, создающая экземпляр ViewModel.
 */
class ViewModelFactory<VM : ViewModel>(
    private val viewModelCreator: () -> VM
) : ViewModelProvider.Factory {

    /**
     * Создает новый экземпляр ViewModel.
     *
     * @param modelClass Класс ViewModel.
     * @return Созданный экземпляр ViewModel.
     * @throws ClassCastException если тип ViewModel не соответствует ожидаемому.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelCreator() as T
    }
}

/**
 * Расширение для [Fragment] для создания ViewModel с помощью пользовательского создателя.
 *
 * @param VM Тип ViewModel.
 * @param creator Функция, создающая экземпляр ViewModel.
 * @return Лениво инициализированный экземпляр ViewModel.
 */
inline fun <reified VM : ViewModel> Fragment.viewModelCreator(noinline creator: () -> VM): Lazy<VM> {
    return viewModels { ViewModelFactory(creator) }
}

/**
 * Расширение для [AppCompatActivity] для создания ViewModel с помощью пользовательского создателя.
 *
 * @param VM Тип ViewModel.
 * @param creator Функция, создающая экземпляр ViewModel.
 * @return Лениво инициализированный экземпляр ViewModel.
 */
inline fun <reified VM : ViewModel> AppCompatActivity.viewModelCreator(noinline creator: () -> VM): Lazy<VM> {
    return viewModels { ViewModelFactory(creator) }
}
