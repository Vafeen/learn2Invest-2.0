package ru.surf.learn2invest.presentation.utils

import android.text.Editable
import android.text.TextWatcher

internal fun textListener(
    beforeTextChanged: ((s: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null,
    onTextChanged: ((s: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null,
    afterTextChanged: ((s: Editable?) -> Unit)? = null,
): TextWatcher =
    object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?, start: Int, count: Int, after: Int
        ) {
            beforeTextChanged?.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged?.invoke(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged?.invoke(s)
        }
    }