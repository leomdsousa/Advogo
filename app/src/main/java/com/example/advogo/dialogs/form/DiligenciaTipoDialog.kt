package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.advogo.R
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils

abstract class DiligenciaTipoDialog(
    context: Context,
    private var diligenciaTipo: DiligenciaTipo,
    private val binding: DialogListFormBinding,
    private val readOnly: Boolean = false
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(diligenciaTipo)

        if(readOnly) {
            binding.btnSubmit.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_list_form)
        }

        binding.btnSubmit.setOnClickListener {
            dismiss()
            onSubmit(diligenciaTipo)
        }
    }

    private fun setDados(diligenciaTipo: DiligenciaTipo) {
        if(diligenciaTipo.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Diligência Tipo"
            binding.etDescription.text = null
            binding.etTipo.visibility = View.GONE

            binding.btnSubmit.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Diligência Tipo"
            binding.etDescription.setText(diligenciaTipo.tipo)
            binding.etTipo.visibility = View.GONE
            
            binding.btnSubmit.text = "Atualizar"
        }
    }

    protected abstract fun onSubmit(diligenciaTipo: DiligenciaTipo)
}