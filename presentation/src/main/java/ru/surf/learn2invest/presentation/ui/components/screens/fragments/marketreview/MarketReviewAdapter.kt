package ru.surf.learn2invest.presentation.ui.components.screens.fragments.marketreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ActivityContext
import ru.surf.learn2invest.domain.domain_models.CoinReview
import ru.surf.learn2invest.domain.services.coin_icon_loader.usecase.LoadCoinIconUseCase
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_review.AssetReviewActivity
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.round
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

internal class MarketReviewAdapter @Inject constructor(
    private val loadCoinIconUseCase: LoadCoinIconUseCase,
    @ActivityContext var context: Context
) : RecyclerView.Adapter<MarketReviewAdapter.ViewHolder>() {
    var data: List<CoinReview> = listOf()
        set(value) {
            val oldList = field
            val diffCallback = MarketReviewAdapterDiffCallback(oldList, value)
            val diffs = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffs.dispatchUpdatesTo(this)
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon = itemView.findViewById<ImageView>(R.id.coin_icon)
        val coinTopTextInfo = itemView.findViewById<TextView>(R.id.coin_name)
        val coinBottomTextInfo = itemView.findViewById<TextView>(R.id.coin_symbol)
        val coinTopNumericInfo = itemView.findViewById<TextView>(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo = itemView.findViewById<TextView>(R.id.coin_bottom_numeric_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.coin_item, parent, false)
    )


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coin = data[position]
        holder.apply {
            coinTopTextInfo.text = if (coin.name.length > 10)
                StringBuilder(coin.name).insert(10, '\n')
            else
                coin.name
            coinBottomTextInfo.text = coin.symbol
            coinTopNumericInfo.text =
                NumberFormat.getInstance(Locale.US).apply {
                    maximumFractionDigits = 4
                }.format(coin.priceUsd).getWithCurrency()
            if (coin.changePercent24Hr >= 0) {
                coinBottomNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.increase))
            } else coinBottomNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.recession))
            coinBottomNumericInfo.text = "${coin.changePercent24Hr.round()}%"
            loadCoinIconUseCase.invoke(coinIcon, coin.symbol)
            itemView.setOnClickListener {
                context.startActivity(Intent(context, AssetReviewActivity::class.java).apply {
                    putExtras(Bundle().apply {
                        putString(AssetConstants.ID.key, coin.id)
                        putString(AssetConstants.NAME.key, coin.name)
                        putString(AssetConstants.SYMBOL.key, coin.symbol)
                    })
                })
            }
        }
    }
}