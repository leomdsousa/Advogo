package com.example.advogo.utils.masks

import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText

class ProcessMaskTextWatcher(private val editText: AppCompatEditText) : TextWatcher {
    private var isUpdating = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        if (isUpdating) {
            isUpdating = false
            return
        }

        val processo = editable.toString()
        val processoWithMask = applyProcessoMask(processo)
        isUpdating = true
        editText.setText(processoWithMask)
        editText.setSelection(processoWithMask.length)
    }

    private fun applyProcessoMask(processo: String): String {
        val processedDigits = processo.replace("[^0-9]".toRegex(), "")
        val formattedProcesso = StringBuilder()

        if (processedDigits.length > 15) {
            // Limit the input to 15 digits
            formattedProcesso.append(processedDigits.substring(0, 15))
        } else {
            formattedProcesso.append(processedDigits)
        }

        if (formattedProcesso.length > 10) {
            formattedProcesso.insert(10, "/")
        }

        if (formattedProcesso.length > 6) {
            formattedProcesso.insert(6, ".")
        }

        if (formattedProcesso.length > 2) {
            formattedProcesso.insert(2, ".")
        }

        return formattedProcesso.toString()
    }
}