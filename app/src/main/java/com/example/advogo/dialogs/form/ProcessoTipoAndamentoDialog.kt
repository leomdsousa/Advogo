package com.example.advogo.dialogs.form

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.advogo.R
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.databinding.DialogProcessoTipoAndamentoFormBinding
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils

abstract class ProcessoTipoAndamentoDialog(
    context: Context,
    private var processoTipoAndamento: ProcessoTipoAndamento,
    private val binding: DialogProcessoTipoAndamentoFormBinding,
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

        binding.etPrazo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString()

                if(value.isNotEmpty() && value.toInt() > 0) {
                    binding.chkSomenteDiasUteis.visibility = View.VISIBLE
                } else {
                    binding.chkSomenteDiasUteis.visibility = View.GONE
                    binding.chkSomenteDiasUteis.isChecked = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnSubmit.setOnClickListener {
            dismiss()
            onSubmit(processoTipoAndamento)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDados(processoTipoAndamento: ProcessoTipoAndamento) {
        binding.tilTipo.visibility = View.GONE

        if(processoTipoAndamento.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Tipo Andamento"
            binding.etDescription.text = null

            binding.etPrazo.text = null

            binding.chkSomenteDiasUteis.visibility = View.GONE
            binding.chkSomenteDiasUteis.isChecked = false

            binding.btnSubmit.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Tipo Andamento"
            binding.etDescription.setText(processoTipoAndamento.tipo)

            val prazoText = processoTipoAndamento.prazo?.toString()
            binding.etPrazo.text = prazoText?.let { Editable.Factory.getInstance().newEditable(it) }

            if (prazoText != null) {
                if(prazoText.isNotEmpty() && prazoText.toInt() > 0) {
                    binding.chkSomenteDiasUteis.isChecked = processoTipoAndamento.somenteDiaUtil == true
                } else {
                    binding.chkSomenteDiasUteis.visibility = View.GONE
                    binding.chkSomenteDiasUteis.isChecked = false
                }
            } else {
                binding.chkSomenteDiasUteis.visibility = View.GONE
                binding.chkSomenteDiasUteis.isChecked = false
            }

            binding.btnSubmit.text = "Atualizar"
        }
    }

    protected abstract fun onSubmit(processoTipoAndamento: ProcessoTipoAndamento)
}