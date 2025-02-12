package ru.surf.learn2invest.presentation.ui.components.screens.fragments.subhistory

import android.content.Context
import android.content.res.Configuration
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
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.common.TransactionAdapterDiffCallback
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import javax.inject.Inject

/**
 * Адаптер для отображения истории сделок с криптовалютой в RecyclerView.
 * В этом адаптере используются такие данные, как имя монеты, ее символ,
 * цена сделки и количество.
 */
internal class SubHistoryAdapter @Inject constructor(
    private val loadCoinIconUseCase: LoadCoinIconUseCase, // Используется для загрузки иконки монеты
    @ActivityContext var context: Context, // Контекст активности для получения цветов и других ресурсов
) : RecyclerView.Adapter<SubHistoryAdapter.ViewHolder>() {

    // Список данных о транзакциях
    var data: List<Transaction> = listOf()
        set(value) {
            val oldList = field
            val diffCallback = TransactionAdapterDiffCallback(oldList, value)
            val diffs = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffs.dispatchUpdatesTo(this) // Обновление адаптера с использованием diffing
        }

    /**
     * ViewHolder для одного элемента списка.
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coinIcon: ImageView = itemView.findViewById(R.id.coin_icon)
        val coinTopTextInfo: TextView = itemView.findViewById(R.id.coin_name)
        val coinBottomTextInfo: TextView = itemView.findViewById(R.id.coin_symbol)
        val coinTopNumericInfo: TextView = itemView.findViewById(R.id.coin_top_numeric_info)
        val coinBottomNumericInfo: TextView = itemView.findViewById(R.id.coin_bottom_numeric_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.coin_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    /**
     * Заполнение данных в view на основе позиции в списке.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            coinTopTextInfo.text =
                if (data[position].name.length > 12) "${data[position].name.substring(0..12)}..."
                else data[position].name
            coinBottomTextInfo.text = data[position].amount.toString()

            // Обработка данных в зависимости от типа транзакции (покупка или продажа)
            if (data[position].transactionType == TransactionsType.Sell) {
                coinTopNumericInfo.text = "+ ${data[position].dealPrice.getWithCurrency()}"
                coinTopNumericInfo.setTextColor(coinBottomNumericInfo.context.getColor(R.color.increase))
            } else {
                coinTopNumericInfo.text = "- ${data[position].dealPrice.getWithCurrency()}"
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
            coinBottomNumericInfo.text = "${data[position].coinPrice}$"
            loadCoinIconUseCase.invoke(coinIcon, data[position].symbol) // Загрузка иконки монеты
        }
    }
}