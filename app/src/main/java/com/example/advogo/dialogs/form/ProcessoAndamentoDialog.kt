package com.example.advogo.dialogs.form

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.DialogProcessoAndamentoBinding
import com.example.advogo.dialogs.ProcessoAndamentoStatusDialog
import com.example.advogo.dialogs.ProcessoAndamentoTiposDialog
import com.example.advogo.models.*
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.DialogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        setDados(andamento)

        if(readOnly) {
            binding.btnSubmitProcessoAndamento.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_processo_andamento)
        }

        binding.etDataAndamento.setOnClickListener {
            onChooseDate()
        }

        binding.etTipoAndamentoProcesso.setOnClickListener {
            tiposAndamentoProcessoDialog()
        }

        binding.etStatusAndamentoProcesso.setOnClickListener {
            statusAndamentoProcessoDialog()
        }

        binding.btnSubmitProcessoAndamento.setOnClickListener {
            dismiss()
            onSubmit(andamento)
        }

    }

    private fun tiposAndamentoProcessoDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            val listDialog = object : ProcessoAndamentoTiposDialog(
                context,
                tiposAndamentos,
            ) {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(item: ProcessoTipoAndamento, action: String) {
                    if (action == Constants.SELECIONAR) {
                        tiposAndamentos.forEach {
                            it.selecionado = false
                        }

                        if (binding.tipoHidden.text.toString() != item.id) {
                            binding.etTipoAndamentoProcesso.setText(item.tipo)
                            binding.tipoHidden.text = item.id
                            tipoAndamentoSelecionado = item.id
                            tiposAndamentos[tiposAndamentos.indexOf(item)].selecionado = true

                            binding.tvPrazoTipoAndamento.visibility = View.VISIBLE
                            binding.tvSomenteDiasUteisTipoAndamento.visibility = View.VISIBLE

                            binding.tvPrazoTipoAndamento.text = "Prazo: ${item.prazo ?: "Não informado"}"
                            binding.tvSomenteDiasUteisTipoAndamento.text = "Somente dias úteis: ${if (item.somenteDiaUtil == null) "Não informado" else if(item.somenteDiaUtil == true) "Sim" else "Não"}"
                        } else {
                            Toast.makeText(
                                context,
                                "Tipo já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etTipoAndamentoProcesso.text = null
                        binding.tipoHidden.text = null
                        tipoAndamentoSelecionado = null
                        tiposAndamentos[tiposAndamentos.indexOf(item)].selecionado = false

                        binding.tvPrazoTipoAndamento.visibility = View.GONE
                        binding.tvSomenteDiasUteisTipoAndamento.visibility = View.GONE

                        binding.tvPrazoTipoAndamento.text = null
                        binding.tvSomenteDiasUteisTipoAndamento.text = null
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun statusAndamentoProcessoDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            val listDialog = object : ProcessoAndamentoStatusDialog(
                context,
                statusAndamentos,
            ) {
                override fun onItemSelected(item: ProcessoStatusAndamento, action: String) {
                    if (action == Constants.SELECIONAR) {
                        statusAndamentos.forEach {
                            it.selecionado = false
                        }

                        if (binding.statusHidden.text.toString() != item.id) {
                            binding.etStatusAndamentoProcesso.setText(item.status)
                            binding.statusHidden.text = item.id
                            statusAndamentoSelecionado = item.id
                            statusAndamentos[statusAndamentos.indexOf(item)].selecionado = true
                        } else {
                            Toast.makeText(
                                context,
                                "Status já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etStatusAndamentoProcesso.text = null
                        binding.statusHidden.text = null
                        statusAndamentoSelecionado = null
                        statusAndamentos[statusAndamentos.indexOf(item)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDados(andamento: ProcessoAndamento) {
        if(andamento.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Andamento"
            binding.btnSubmitProcessoAndamento.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Andamento"
            binding.btnSubmitProcessoAndamento.text = "Atualizar"

            binding.etDescricaoAndamento.setText(andamento.descricao)

            binding.etTipoAndamentoProcesso.setText(andamento.tipoObj?.tipo)
            tipoAndamentoSelecionado = andamento.tipoObj?.id
            binding.tipoHidden.text = andamento.tipoObj?.id;

            binding.etStatusAndamentoProcesso.setText(andamento.statusObj?.status)
            statusAndamentoSelecionado = andamento.statusObj?.id
            binding.statusHidden.text = andamento.statusObj?.id;

            binding.advHidden.text = andamento.advogadoObj?.id;

            if(andamento.data?.isNotEmpty() == true) {
                val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                val fromDate = fromFormat.parse(andamento.data)
                val selectedDate = toFormat.format(fromDate)
                binding.etDataAndamento.setText(selectedDate)
            }

            if(!andamento.tipo.isNullOrBlank() && andamento.tipoObj != null) {
                binding.tvPrazoTipoAndamento.visibility = View.VISIBLE
                binding.tvSomenteDiasUteisTipoAndamento.visibility = View.VISIBLE

                binding.tvPrazoTipoAndamento.text = "Prazo: ${andamento.tipoObj?.prazo ?: "Não informado"}"
                binding.tvSomenteDiasUteisTipoAndamento.text = "Somente dias úteis: ${if (andamento.tipoObj?.somenteDiaUtil == null) "Não informado" else if(andamento.tipoObj?.somenteDiaUtil == true) "Sim" else "Não"}"
            }
        }
    }

    protected abstract fun onSubmit(andamento: ProcessoAndamento)
    protected abstract fun onChooseDate()
}