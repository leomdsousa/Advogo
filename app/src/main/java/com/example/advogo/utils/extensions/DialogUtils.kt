package com.example.advogo.utils.extensions

import android.app.Dialog
import android.view.ViewGroup
import android.widget.EditText
import com.example.advogo.R

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

    fun ViewGroup.getEditTextsRecursively(): List<EditText> {
        val editTexts = mutableListOf<EditText>()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewGroup) {
                editTexts.addAll(child.getEditTextsRecursively())
            } else if (child is EditText) {
                editTexts.add(child)
            }
        }
        return editTexts
    }
}