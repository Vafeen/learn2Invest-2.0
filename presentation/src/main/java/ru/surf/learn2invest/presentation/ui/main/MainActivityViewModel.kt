package ru.surf.learn2invest.presentation.ui.main

import android.view.View
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.domain.animator.usecase.AnimateAlphaUseCase
import ru.surf.learn2invest.domain.domain_models.Profile
import ru.surf.learn2invest.domain.services.ProfileManager
import javax.inject.Inject

/**
 * ViewModel для экрана главной активности. Управляет данными профиля пользователя и анимациями.
 *
 * @param profileManager Менеджер профиля, предоставляющий данные профиля и возможности для обновления профиля.
 * @param animateAlphaUseCase Используется для анимации изменения прозрачности (alpha) у вида.
 */
@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    private val profileManager: ProfileManager,
    private val animateAlphaUseCase: AnimateAlphaUseCase,
) : ViewModel() {

    /**
     * Поток, содержащий данные профиля пользователя.
     * Используется для наблюдения за изменениями профиля в UI.
     */
    val profileFlow = profileManager.profileFlow

    /**
     * Функция для обновления данных профиля.
     * Вызывает метод обновления профиля в [profileManager].
     *
     * @param updating Лямбда-функция, которая принимает текущий объект [Profile] и возвращает обновлённый [Profile].
     */
    suspend fun updateProfile(updating: (Profile) -> Profile) {
        profileManager.updateProfile(updating)
    }

    /**
     * Функция для инициализации профиля пользователя.
     * Вызывает метод инициализации профиля в [profileManager].
     */
    suspend fun initProfile() {
        profileManager.initProfile()
    }

    /**
     * Функция для анимации изменения прозрачности (alpha) вида.
     *
     * @param view Вид, прозрачность которого будет анимирована.
     * @param duration Длительность анимации в миллисекундах.
     * @param onStart Колбэк, который будет вызван перед началом анимации (необязательный).
     * @param onEnd Колбэк, который будет вызван по завершению анимации (необязательный).
     * @param onCancel Колбэк, который будет вызван, если анимация была отменена (необязательный).
     * @param onRepeat Колбэк, который будет вызван при повторении анимации (необязательный).
     * @param values Массив значений альфа-прозрачности, который будет анимирован.
     */
    fun animateAlpha(
        view: View,
        duration: Long,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onRepeat: (() -> Unit)? = null,
        values: FloatArray,
    ) = animateAlphaUseCase.invoke(view, duration, onStart, onEnd, onCancel, onRepeat, values)
}
