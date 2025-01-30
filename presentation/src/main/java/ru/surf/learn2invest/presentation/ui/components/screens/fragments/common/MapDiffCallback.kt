package ru.surf.learn2invest.presentation.ui.components.screens.fragments.common

import androidx.recyclerview.widget.DiffUtil

internal class MapDiffCallback(
    private val oldMap: Map<String, Float>,
    private val newMap: Map<String, Float>
) : DiffUtil.Callback() {

    private val oldList = oldMap.toList()
    private val newList = newMap.toList()

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].first == newList[newItemPosition].first

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}
