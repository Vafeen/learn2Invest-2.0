package ru.surf.learn2invest.presentation.ui.components.screens.fragments.portfolio

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
import ru.surf.learn2invest.domain.domain_models.AssetInvest
import ru.surf.learn2invest.domain.services.coin_icon_loader.usecase.LoadCoinIconUseCase
import ru.surf.learn2invest.presentation.R
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_review.AssetReviewActivity
import ru.surf.learn2invest.presentation.ui.components.screens.fragments.common.MapDiffCallback
import ru.surf.learn2invest.presentation.utils.AssetConstants
import ru.surf.learn2invest.presentation.utils.getWithCurrency
import ru.surf.learn2invest.presentation.utils.getWithPCS
import ru.surf.learn2invest.presentation.utils.round
import javax.inject.Inject


/**
 * Адаптер для отображения портфеля активов.
 *
 * @param loadCoinIconUseCase Используется для загрузки иконки монеты.
 * @param context Контекст, необходим для старта активностей.
 */
internal class PortfolioAdapter @Inject constructor(
    private val loadCoinIconUseCase: LoadCoinIconUseCase,
    @ActivityContext var context: Context,
) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    /**
     * Список активов для отображения.
     * Когда список изменяется, рассчитываются различия и обновляются данные в адаптере.
     */
    var assets: List<AssetInvest> = emptyList()
        set(value) {
            val oldList = field
            val diffCallback = PortfolioAdapterDiffCallback(oldList, value)
            val diffs = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffs.dispatchUpdatesTo(this) // Обновление адаптера с учетом изменений в списке
        }

    /**
     * Словарь с изменениями цен для каждого актива.
     * Когда изменения цен обновляются, рассчитываются различия и обновляются данные в адаптере.
     */
    var priceChanges: Map<String, Float> = emptyMap()
        set(value) {
            val oldList = field
            val diffCallback = MapDiffCallback(oldList, value)
            val diffs = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffs.dispatchUpdatesTo(this) // Обновление адаптера с учетом изменений в ценах
        }

    /**
     * Создает и возвращает ViewHolder для отображения данных.
     *
     * @param parent Родительский элемент для размещения представления.
     * @param viewType Тип представления (не используется в данном примере).
     * @return Новый экземпляр PortfolioViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coin_item, parent, false)
        return PortfolioViewHolder(view)
    }

    /**
     * Привязывает данные актива к соответствующему элементу списка.
     *
     * @param holder ViewHolder для привязки данных.
     * @param position Позиция элемента в списке.
     */
    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val asset = assets[position] // Получаем актив по позиции
        holder.bind(asset, priceChanges[asset.symbol] ?: 0f) // Привязываем данные к элементу
        holder.itemView.setOnClickListener {
            // При клике на элемент открываем экран с подробностями актива
            context.startActivity(Intent(context, AssetReviewActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString(AssetConstants.ID.key, asset.assetID) // ID актива
                    putString(AssetConstants.NAME.key, asset.name) // Название актива
                    putString(AssetConstants.SYMBOL.key, asset.symbol) // Символ актива
                })
            })
        }
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return Количество элементов в списке активов.
     */
    override fun getItemCount(): Int = assets.size

    /**
     * ViewHolder для отображения данных актива в элементе списка.
     */
    inner class PortfolioViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        // Элементы UI для отображения информации об активе
        private val coinIcon: ImageView = itemView.findViewById(R.id.coin_icon)
        private val coinName: TextView = itemView.findViewById(R.id.coin_name)
        private val coinQuantity: TextView = itemView.findViewById(R.id.coin_symbol)
        private val coinTopNumericInfo: TextView = itemView.findViewById(R.id.coin_top_numeric_info)
        private val coinBottomNumericInfo: TextView = itemView.findViewById(R.id.coin_bottom_numeric_info)

        /**
         * Привязывает данные активов к UI-элементам.
         *
         * @param asset Данные актива для отображения.
         * @param priceChange Изменение цены для актива.
         */
        fun bind(asset: AssetInvest, priceChange: Float) {
            coinName.text = asset.name // Устанавливаем название актива
            coinQuantity.text = "${asset.amount}".getWithPCS(context) // Устанавливаем количество актива
            coinTopNumericInfo.text = priceChange.getWithCurrency() // Устанавливаем цену
            val priceChangePercent = ((priceChange - asset.coinPrice) / asset.coinPrice) * 100 // Рассчитываем изменение цены в процентах
            val roundedPercent = priceChangePercent.round() // Округляем процентное изменение
            coinBottomNumericInfo.setTextColor(
                when {
                    roundedPercent > 0 -> {
                        coinBottomNumericInfo.text = "+$roundedPercent%" // Положительный процент
                        itemView.context.getColor(R.color.increase) // Цвет для роста
                    }

                    roundedPercent < 0 -> {
                        coinBottomNumericInfo.text = "$roundedPercent%" // Отрицательный процент
                        itemView.context.getColor(R.color.recession) // Цвет для падения
                    }

                    else -> {
                        coinBottomNumericInfo.text = "$roundedPercent%" // Без изменений
                        itemView.context.getColor(R.color.black) // Нейтральный цвет
                    }
                }
            )
            loadCoinIconUseCase.invoke(coinIcon, asset.symbol) // Загружаем иконку актива
        }
    }
}
