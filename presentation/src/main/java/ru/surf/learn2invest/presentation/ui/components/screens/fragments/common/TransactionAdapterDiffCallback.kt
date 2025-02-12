package ru.surf.learn2invest.presentation.ui.components.screens.fragments.common

import androidx.recyclerview.widget.DiffUtil
import ru.surf.learn2invest.domain.domain_models.Transaction
/**
 * Класс для сравнения изменений в списке транзакций с использованием `DiffUtil.Callback`.
 * Используется для оптимизации обновлений данных в адаптерах RecyclerView, когда необходимо сравнивать
 * старый и новый список транзакций, чтобы минимизировать количество обновлений.
 *
 * @param oldList Старая коллекция транзакций.
 * @param newList Новая коллекция транзакций.
 */
internal class TransactionAdapterDiffCallback(
    private val oldList: List<Transaction>,
    private val newList: List<Transaction>
) : DiffUtil.Callback() {

    /**
     * Возвращает размер старой коллекции транзакций.
     *
     * @return Размер старой коллекции транзакций.
     */
    override fun getOldListSize(): Int = oldList.size

    /**
     * Возвращает размер новой коллекции транзакций.
     *
     * @return Размер новой коллекции транзакций.
     */
    override fun getNewListSize(): Int = newList.size

    /**
     * Проверяет, являются ли два элемента идентичными по позиции в старом и новом списках.
     * В данном случае сравнивается поле `id` транзакции.
     *
     * @param oldItemPosition Позиция элемента в старом списке.
     * @param newItemPosition Позиция элемента в новом списке.
     * @return true, если идентификаторы транзакций одинаковы, иначе false.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    /**
     * Проверяет, идентичны ли содержимое элементов по позиции в старом и новом списках.
     * В данном случае проверяется, одинаковы ли все поля транзакции.
     *
     * @param oldItemPosition Позиция элемента в старом списке.
     * @param newItemPosition Позиция элемента в новом списке.
     * @return true, если транзакции идентичны, иначе false.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
