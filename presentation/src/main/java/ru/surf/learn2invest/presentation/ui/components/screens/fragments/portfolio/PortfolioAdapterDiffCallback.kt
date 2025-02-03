package ru.surf.learn2invest.presentation.ui.components.screens.fragments.portfolio

import androidx.recyclerview.widget.DiffUtil
import ru.surf.learn2invest.domain.domain_models.AssetInvest

internal class PortfolioAdapterDiffCallback(
    private val oldList: List<AssetInvest>,
    private val newList: List<AssetInvest>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition] // because it's data classes

}