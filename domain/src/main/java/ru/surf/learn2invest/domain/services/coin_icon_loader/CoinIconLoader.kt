package ru.surf.learn2invest.domain.services.coin_icon_loader

import android.widget.ImageView

interface CoinIconLoader {
    fun loadIcon(imageView: ImageView, symbol: String): Request
    interface Request {
        fun cancel()
    }
}