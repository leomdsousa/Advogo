package com.example.advogo.dialogs.form

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.DialogProcessoParteBinding
import com.example.advogo.dialogs.ProcessoTiposPartesDialog
import com.example.advogo.models.*
import com.example.advogo.utils.DialogUtils
import com.example.advogo.utils.constants.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ProcessoParteDialog(
    context: Context,
    private var parte: ProcessoParte,
    private val binding: DialogProcessoParteBinding,
    private var tipoPartes: List<TiposPartes>,
    private val readOnly: Boolean = false
): Dialog(context) {
    private var tipoParteSelecionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(parte)

        if(readOnly) {
            binding.btnSubmitProcessoHistorico.visibility = View.GONE
            DialogUtils.makeEditTextsReadOnly(this, R.layout.dialog_processo_parte)
        }

        binding.etTipoParte.setOnClickListener {
            tiposPartesDialog()
        }

        binding.btnSubmitProcessoHistorico.setOnClickListener {
            dismiss()
            onSubmit(parte)
        }
    }

    private fun setDados(parte: ProcessoParte) {
        if(parte.nome.isNullOrEmpty()) {
            binding.tvTitle.text = "Cadastro Parte"
            binding.etNomeParte.text = null

            binding.btnSubmitProcessoHistorico.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Parte"
            binding.etNomeParte.setText(parte.nome)
            binding.etDocumentoParte.setText(parte.documento)
            binding.etContatoParte.setText(parte.contato)

            binding.etTipoParte.setText(parte.tipoObj?.tipo)
            binding.tipoHidden.text = parte.tipoObj?.id
            tipoParteSelecionado = parte.tipoObj?.id

            binding.btnSubmitProcessoHistorico.text = "Atualizar"
        }
    }

    private fun tiposPartesDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            val listDialog = object : ProcessoTiposPartesDialog(
                context,
                tipoPartes,
            ) {
                override fun onItemSelected(tipoParte: TiposPartes, action: String) {
                    if (action == Constants.SELECIONAR) {
                        tipoPartes.forEach {
                            it.selecionado = false
                        }

                        if (binding.tipoHidden.text.toString() != tipoParte.id) {
                            binding.etTipoParte.setText(tipoParte.tipo)
                            binding.tipoHidden.text = tipoParte.id
                            tipoParteSelecionado = tipoParte.id
                            tipoPartes[tipoPartes.indexOf(tipoParte)].selecionado = true
                        } else {
                            Toast.makeText(
                                context,
                                "Tipo já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etTipoParte.text = null
                        binding.tipoHidden.text = null
                        tipoParteSelecionado = null
                        tipoPartes[tipoPartes.indexOf(tipoParte)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    protected abstract fun onSubmit(parte: ProcessoParte)
}