package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.advogo.R
import com.example.advogo.databinding.DialogDiligenciaHistoricoBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils

abstract class DiligenciaHistoricoDialog(
    context: Context,
    private var historico: DiligenciaHistorico,
    private var binding: DialogDiligenciaHistoricoBinding,
    private val readOnly: Boolean = false
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(historico)

        if(readOnly) {
            binding.btnSubmitDiligenciaHistorico.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_diligencia_historico)
        }

        binding.btnSubmitDiligenciaHistorico.setOnClickListener {
            dismiss()
            onSubmit(historico)
        }
    }

    private fun setDados(historico: DiligenciaHistorico) {
        if(historico.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Histórico"
            binding.etDescricaoHistorico.text = null

            binding.btnSubmitDiligenciaHistorico.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Histórico"
            binding.etDescricaoHistorico.setText(historico.obs)
            
            binding.btnSubmitDiligenciaHistorico.text = "Atualizar"
        }
    }

    protected abstract fun onSubmit(historico: DiligenciaHistorico)
}