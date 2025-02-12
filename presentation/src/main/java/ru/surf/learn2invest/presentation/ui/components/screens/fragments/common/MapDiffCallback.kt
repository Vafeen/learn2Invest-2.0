package ru.surf.learn2invest.presentation.ui.components.screens.fragments.common

import androidx.recyclerview.widget.DiffUtil
/**
 * Класс для сравнения изменений между двумя картами с использованием `DiffUtil.Callback`.
 * Используется для оптимизации обновлений данных в адаптерах RecyclerView, когда необходимо сравнивать
 * старую и новую версию карты, чтобы минимизировать количество обновлений.
 *
 * @param oldMap Старая карта, которая содержит данные перед изменениями.
 * @param newMap Новая карта, которая содержит данные после изменений.
 */
internal class MapDiffCallback(
    private val oldMap: Map<String, Float>,
    private val newMap: Map<String, Float>
) : DiffUtil.Callback() {

    // Преобразуем карты в списки для упрощения работы с DiffUtil
    private val oldList = oldMap.toList()
    private val newList = newMap.toList()

    /**
     * Возвращает размер старой коллекции (карты).
     *
     * @return Размер старой коллекции.
     */
    override fun getOldListSize(): Int = oldList.size

    /**
     * Возвращает размер новой коллекции (карты).
     *
     * @return Размер новой коллекции.
     */
    override fun getNewListSize(): Int = newList.size

    /**
     * Проверяет, являются ли два элемента идентичными по позиции в старом и новом списках.
     * В данном случае сравниваются только ключи (строки) карты.
     *
     * @param oldItemPosition Позиция элемента в старом списке.
     * @param newItemPosition Позиция элемента в новом списке.
     * @return true, если ключи элементов одинаковы, иначе false.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].first == newList[newItemPosition].first

    /**
     * Проверяет, идентичны ли содержимое элементов по позиции в старом и новом списках.
     * В данном случае проверяется и ключ, и значение в карте.
     *
     * @param oldItemPosition Позиция элемента в старом списке.
     * @param newItemPosition Позиция элемента в новом списке.
     * @return true, если элементы одинаковы (ключи и значения), иначе false.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
