package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.advogo.R
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils

abstract class ProcessoTipoAndamentoDialog(
    context: Context,
    private var processoTipoAndamento: ProcessoTipoAndamento,
    private val binding: DialogListFormBinding,
    private val readOnly: Boolean = false
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(processoTipoAndamento)

        if(readOnly) {
            binding.btnSubmit.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_list_form)
        }

        binding.btnSubmit.setOnClickListener {
            dismiss()
            onSubmit(processoTipoAndamento)
        }
    }

    private fun setDados(processoTipoAndamento: ProcessoTipoAndamento) {
        if(processoTipoAndamento.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Tipo Andamento"
            binding.etDescription.text = null
            binding.etTipo.visibility = View.GONE

            binding.btnSubmit.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Tipo Andamento"
            binding.etDescription.setText(processoTipoAndamento.tipo)
            binding.etTipo.visibility = View.GONE
            
            binding.btnSubmit.text = "Atualizar"
        }
    }

    protected abstract fun onSubmit(processoTipoAndamento: ProcessoTipoAndamento)
}