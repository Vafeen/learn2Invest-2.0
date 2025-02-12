package ru.surf.learn2invest.presentation.utils

import android.content.Context
import androidx.annotation.StringRes



internal fun Context.getVersionName(): String? =
    packageManager.getPackageInfo(packageName, 0).versionName