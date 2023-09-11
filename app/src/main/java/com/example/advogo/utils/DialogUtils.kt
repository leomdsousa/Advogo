package com.example.advogo.utils

import android.app.Dialog
import android.view.ViewGroup
import com.example.advogo.R
import com.example.advogo.utils.extensions.ViewGroupExtensions.getEditTextsRecursively

object DialogUtils {
    fun makeEditTextsReadOnly(dialog: Dialog, layoutId: Int) {
        val editTexts = dialog.findViewById<ViewGroup>(layoutId)?.getEditTextsRecursively()

        editTexts?.forEach { editText ->
            editText.setBackgroundResource(R.drawable.til_background)
            editText.isFocusable = false
            editText.isClickable = false
            editText.isLongClickable = false
        }
    }
}