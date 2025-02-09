package ru.surf.learn2invest.presentation.ui.components.screens.fragments.asset_review

import android.widget.ImageView
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.services.coin_icon_loader.CoinIconLoader
import ru.surf.learn2invest.domain.services.coin_icon_loader.usecase.LoadCoinIconUseCase
import javax.inject.Inject

@HiltViewModel
internal class AssetReviewActivityViewModel @Inject constructor(private val loadCoinIconUseCase: LoadCoinIconUseCase) :
    ViewModel() {
    private var imageLoaderRequest: CoinIconLoader.Request? = null
    fun loadImage(imageView: ImageView, symbol: String) {
        imageLoaderRequest = loadCoinIconUseCase.invoke(imageView, symbol)
    }

    fun cancelLoadingImage() {
        imageLoaderRequest?.cancel()
    }
}