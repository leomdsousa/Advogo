package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosStatusAndamentosAdapter
import com.example.advogo.adapters.ProcessosTiposAndamentosAdapter
import com.example.advogo.databinding.DialogProcessoAndamentoBinding
import com.example.advogo.models.*
import com.example.advogo.utils.extensions.DialogUtils
import java.text.SimpleDateFormat
import java.util.*

abstract class ProcessoAndamentoDialog(
    context: Context,
    private var andamento: ProcessoAndamento,
    private val binding: DialogProcessoAndamentoBinding,
    private var tiposAndamentos: List<ProcessoTipoAndamento>,
    private var statusAndamentos: List<ProcessoStatusAndamento>,
    private val readOnly: Boolean = false
): Dialog(context) {
    private var tipoAndamentoSelecionado: String? = null
    private var statusAndamentoSelecionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupSpinners()
        setDados(andamento)

        if(readOnly) {
            binding.btnSubmitProcessoAndamento.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_processo_andamento)
        }

        binding.etDataAndamento.setOnClickListener {
            onChooseDate()
        }

        binding.btnSubmitProcessoAndamento.setOnClickListener {
            dismiss()
            onSubmit(andamento)
        }

    }

    private fun setupSpinners() {
        setupSpinnerStatusAndamento()
        setupSpinnerTiposAndamento()
    }

    private fun setupSpinnerTiposAndamento() {
        val spinnerTipos = binding.spinnerTipoAndamentoProcesso

        (tiposAndamentos as MutableList<ProcessoTipoAndamento>).add(0, ProcessoTipoAndamento(tipo = "Selecione"))

        val adapter = ProcessosTiposAndamentosAdapter(context, tiposAndamentos)
        spinnerTipos.adapter = adapter

        if(andamento.tipoObj != null)
            binding.spinnerTipoAndamentoProcesso.setSelection(tiposAndamentos.indexOf(andamento.tipoObj))

        spinnerTipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spinnerTipos.selectedItem as? ProcessoTipoAndamento
                selectedItem?.let {
                    tipoAndamentoSelecionado = selectedItem.id
                    spinnerTipos.setSelection(id.toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }
    }

    private fun setupSpinnerStatusAndamento() {
        val spinnerStatus = binding.spinnerStatusProcessoAndamento

        (statusAndamentos as MutableList<ProcessoStatusAndamento>).add(0, ProcessoStatusAndamento(status = "Selecione"))

        val adapter = ProcessosStatusAndamentosAdapter(context, statusAndamentos)
        spinnerStatus.adapter = adapter

        if(andamento.statusObj != null)
            binding.spinnerStatusProcessoAndamento.setSelection(statusAndamentos.indexOf(andamento.statusObj))

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spinnerStatus.selectedItem as? ProcessoTipoAndamento
                selectedItem?.let {
                    statusAndamentoSelecionado = selectedItem.id
                    spinnerStatus.setSelection(id.toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }
    }

    private fun setDados(andamento: ProcessoAndamento) {
        if(andamento.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Andamento"
            binding.btnSubmitProcessoAndamento.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Andamento"
            binding.btnSubmitProcessoAndamento.text = "Atualizar"

            binding.etDescricaoAndamento.setText(andamento.descricao)
            binding.spinnerTipoAndamentoProcesso.setSelection(tiposAndamentos.indexOf(andamento.tipoObj))
            binding.spinnerStatusProcessoAndamento.setSelection(statusAndamentos.indexOf(andamento.statusObj))

            if(andamento.data?.isNotEmpty() == true) {
                val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                val fromDate = fromFormat.parse(andamento.data)
                val selectedDate = toFormat.format(fromDate)
                binding.etDataAndamento.setText(selectedDate)
            }
        }
    }

    protected abstract fun onSubmit(andamento: ProcessoAndamento)
    protected abstract fun onChooseDate()
}