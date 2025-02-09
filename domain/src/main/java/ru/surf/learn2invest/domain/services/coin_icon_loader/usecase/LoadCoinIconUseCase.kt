package ru.surf.learn2invest.domain.services.coin_icon_loader.usecase

import android.widget.ImageView
import ru.surf.learn2invest.domain.services.coin_icon_loader.CoinIconLoader
import javax.inject.Inject

class LoadCoinIconUseCase @Inject constructor(private val coinIconLoader: CoinIconLoader) {
    operator fun invoke(imageView: ImageView, symbol: String): CoinIconLoader.Request =
        coinIconLoader.loadIcon(imageView, symbol)
}