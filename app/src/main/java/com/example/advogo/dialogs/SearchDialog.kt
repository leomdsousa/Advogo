package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.advogo.adapters.AdvogadosAdapter
import com.example.advogo.databinding.DialogSearchBinding
import com.example.advogo.models.Advogado

abstract class SearchDialog(
    context: Context,
    private val titulo: String,
    private val placeholder: String
): Dialog(context) {
    private lateinit var binding: DialogSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogSearchBinding.inflate(layoutInflater)

        binding.tvTitle.text = titulo
        binding.tvSearchText.hint = placeholder

        binding.btnSearch.setOnClickListener {
            dismiss()
            onItemSelected(binding.btnSearch.text.toString())
        }

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }

    protected abstract fun onItemSelected(value: String)
}