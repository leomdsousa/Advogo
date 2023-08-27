package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.advogo.databinding.DialogSearchBinding

abstract class SearchDialog(
    context: Context,
    private val titulo: String,
    private val placeholder: String,
    private val binding: DialogSearchBinding,
    private val defaultSearchText: String? = null
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding.tvTitle.text = titulo
        binding.tvSearchText.hint = placeholder

        if(defaultSearchText != null && defaultSearchText.isNotEmpty()) {
            binding.btnSearch.text = defaultSearchText
        }

        binding.btnSearch.setOnClickListener {
            dismiss()
            onItemSelected(binding.tvSearchText.text.toString())
        }

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    protected abstract fun onItemSelected(value: String)
}