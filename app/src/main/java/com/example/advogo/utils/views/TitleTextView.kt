package com.example.advogo.utils.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TitleTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        applyCustomFont()
    }

    private fun applyCustomFont() {
        val fontFamily = "montserrat_semibold"
        val typeface = Typeface.create(fontFamily, Typeface.NORMAL)
        setTypeface(typeface)
    }
}
