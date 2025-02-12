package ru.surf.learn2invest.presentation.ui.components.screens.fragments.history

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ActivityContext
import ru.surf.learn2invest.domain.TransactionsType
import ru.surf.learn2invest.domain.domain_models.Transaction
import ru.surf.learn2invest.domain.services.coin_icon_loader.usecase.LoadCoinIconUseCase
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_review.AssetReviewActivity
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.common.TransactionAdapterDiffCallback
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import javax.inject.Inject

/**
 * Адаптер для отображения списка транзакций в [HistoryFragment].
 * Используется для отображения данных транзакций, включая иконки монет и другую информацию.
 */
internal class HistoryFragmentAdapter @Inject constructor(
    @ActivityContext var context: Context,
    private val loadCoinIconUseCase: LoadCoinIconUseCase,
) : RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder>() {

    var data: List<Transaction> = listOf()
        set(value) {
            val oldList = field
            val diffCallback = TransactionAdapterDiffCallback(oldList, value)
            val diffs = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffs.dispatchUpdatesTo(this)
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon: ImageView = itemView.findViewById(R.id.coin_icon)
        val coinTopTextInfo: TextView = itemView.findViewById(R.id.coin_name)
        val coinBottomTextInfo: TextView = itemView.findViewById(R.id.coin_symbol)
        val coinTopNumericInfo: TextView = itemView.findViewById(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo: TextView = itemView.findViewById(R.id.coin_bottom_numeric_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.coin_item, parent, false))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coin = data[position]
        holder.apply {
            coinTopTextInfo.text = if (coin.name.length > 12) "${coin.name.substring(0..12)}..."
            else coin.name
            coinBottomTextInfo.text = coin.symbol
            if (coin.transactionType == TransactionsType.Sell) {
                coinTopNumericInfo.text = "+ ${coin.dealPrice.getWithCurrency()}"
                coinTopNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.increase))
            } else {
                coinTopNumericInfo.text = "- ${coin.dealPrice.getWithCurrency()}"
                coinTopNumericInfo.setTextColor(
                    coinBottomNumericInfo.context.getColor(
                        if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                            R.color.white
                        } else {
                            R.color.black
                        }
                    )
                )
            }
            coinBottomNumericInfo.text = coin.coinPrice.getWithCurrency()
            loadCoinIconUseCase.invoke(coinIcon, coin.symbol)
            itemView.setOnClickListener {
                context.startActivity(Intent(context, AssetReviewActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString(AssetConstants.ID.key, coin.coinID)
                        putString(AssetConstants.NAME.key, coin.name)
                        putString(AssetConstants.SYMBOL.key, coin.symbol)
                    })
                })
            }
        }
    }
}
