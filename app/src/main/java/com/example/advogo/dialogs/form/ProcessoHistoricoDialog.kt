package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.advogo.R
import com.example.advogo.databinding.DialogProcessoHistoricoBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils

abstract class ProcessoHistoricoDialog(
    context: Context,
    private var historico: ProcessoHistorico,
    private val binding: DialogProcessoHistoricoBinding,
    private val readOnly: Boolean = false
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(historico)

        if(readOnly) {
            binding.btnSubmitProcessoHistorico.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_processo_historico)
        }

        binding.btnSubmitProcessoHistorico.setOnClickListener {
            dismiss()
            onSubmit(historico)
        }
    }

    private fun setDados(historico: ProcessoHistorico) {
        if(historico.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Histórico"
            binding.etDescricaoHistorico.text = null

            binding.btnSubmitProcessoHistorico.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Histórico"
            binding.etDescricaoHistorico.setText(historico.obs)
            
            binding.btnSubmitProcessoHistorico.text = "Atualizar"
        }
    }

    protected abstract fun onSubmit(historico: ProcessoHistorico)
}