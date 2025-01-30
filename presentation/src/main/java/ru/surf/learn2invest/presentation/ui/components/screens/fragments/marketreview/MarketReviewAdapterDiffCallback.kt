package ru.surf.learn2invest.presentation.ui.components.screens.fragments.marketreview

import androidx.recyclerview.widget.DiffUtil
import ru.surf.learn2invest.domain.domain_models.CoinReview

internal class MarketReviewAdapterDiffCallback(
    private val oldList: List<CoinReview>,
    private val newList: List<CoinReview>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

}