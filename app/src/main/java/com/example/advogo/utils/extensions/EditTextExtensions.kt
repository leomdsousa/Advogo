package com.example.advogo.utils.extensions

import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.example.advogo.R

fun AppCompatEditText.showPasswordVisibilityOnTouch(event: MotionEvent) {
    val drawableRight = compoundDrawables[2]
    if (event.rawX >= right - drawableRight.bounds.width()) {
        (transformationMethod as? PasswordTransformationMethod)?.let {
            transformationMethod = null
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
        } ?: run {
            transformationMethod = PasswordTransformationMethod.getInstance()
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0)
        }
        setSelection(text?.length ?: 0)
    }
}