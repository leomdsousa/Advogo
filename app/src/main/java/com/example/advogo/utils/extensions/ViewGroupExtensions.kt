package com.example.advogo.utils.extensions

import android.view.ViewGroup
import android.widget.EditText

object ViewGroupExtensions {
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