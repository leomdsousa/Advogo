package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.advogo.R
import com.example.advogo.databinding.DialogProcessoParteBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils

abstract class ProcessoParteDialog(
    context: Context,
    private var parte: ProcessoParte,
    private val binding: DialogProcessoParteBinding,
    private val readOnly: Boolean = false
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(parte)

        if(readOnly) {
            binding.btnSubmitProcessoHistorico.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_processo_historico)
        }

        binding.btnSubmitProcessoHistorico.setOnClickListener {
            dismiss()
            onSubmit(parte)
        }
    }

    private fun setDados(parte: ProcessoParte) {
        if(true) {
            binding.tvTitle.text = "Cadastro Parte"
            binding.etNomeParte.text = null

            binding.btnSubmitProcessoHistorico.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Parte"
            binding.etNomeParte.setText(parte.nome)
            binding.etDocumentoParte.setText(parte.documento)
            binding.etContatoParte.setText(parte.contato)
            binding.etTipoParte.setText(parte.tipoObj?.tipo)
            
            binding.btnSubmitProcessoHistorico.text = "Atualizar"
        }
    }

    protected abstract fun onSubmit(parte: ProcessoParte)
}