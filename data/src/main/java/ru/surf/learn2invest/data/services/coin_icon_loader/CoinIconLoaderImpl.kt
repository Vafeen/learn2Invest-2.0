package ru.surf.learn2invest.data.services.coin_icon_loader

import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.Disposable
import ru.surf.learn2invest.domain.services.coin_icon_loader.CoinIconLoader
import ru.surf.learn2invest.resources.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CoinIconLoaderImpl @Inject constructor(private val imageLoader: ImageLoader) :
    CoinIconLoader {
    override fun loadIcon(imageView: ImageView, symbol: String): CoinIconLoader.Request =
        RequestImpl(imageView.load(
            data = "${CoinIconLoaderLinks.API_ICON}${symbol.lowercase()}.svg",
            imageLoader = imageLoader
        )
        {
            placeholder(R.drawable.placeholder)
            error(R.drawable.coin_placeholder)
        })

    inner class RequestImpl(private val disposable: Disposable) :
        CoinIconLoader.Request {
        override fun cancel() {
            disposable.dispose()
        }
    }
}