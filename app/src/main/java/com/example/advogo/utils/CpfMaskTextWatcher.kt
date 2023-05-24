package com.example.advogo.utils

import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText

class CpfMaskTextWatcher(private val editText: AppCompatEditText) : TextWatcher {
    private var isUpdating = false
    private val cpfMask = "###.###.###-##"

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        if (isUpdating) {
            isUpdating = false
            return
        }

        val cpf = editable.toString()
        val cpfWithMask = applyCpfMask(cpf)
        isUpdating = true
        editText.setText(cpfWithMask)
        editText.setSelection(cpfWithMask.length)
    }

    private fun applyCpfMask(cpf: String): String {
        val sb = StringBuilder()
        var index = 0

        for (i in cpfMask.indices) {
            if (cpf.length == index) break

            if (cpfMask[i] == '#') {
                sb.append(cpf[index])
                index++
            } else {
                sb.append(cpfMask[i])
            }
        }

        return sb.toString()
    }
}