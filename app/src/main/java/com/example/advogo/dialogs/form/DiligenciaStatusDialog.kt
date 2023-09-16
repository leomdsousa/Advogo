package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.advogo.R
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils

abstract class DiligenciaStatusDialog(
    context: Context,
    private var diligenciaStatus: DiligenciaStatus,
    private val binding: DialogListFormBinding,
    private val readOnly: Boolean = false
): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(diligenciaStatus)

        if(readOnly) {
            binding.btnSubmit.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_list_form)
        }

        binding.btnSubmit.setOnClickListener {
            dismiss()
            onSubmit(diligenciaStatus)
        }
    }

    private fun setDados(diligenciaStatus: DiligenciaStatus) {
        if(diligenciaStatus.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Diligência Status"
            binding.etDescription.text = null
            binding.etTipo.visibility = View.GONE

            binding.btnSubmit.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Diligência Status"
            binding.etDescription.setText(diligenciaStatus.status)
            binding.etTipo.visibility = View.GONE
            
            binding.btnSubmit.text = "Atualizar"
        }
    }

    protected abstract fun onSubmit(diligenciaStatus: DiligenciaStatus)
}