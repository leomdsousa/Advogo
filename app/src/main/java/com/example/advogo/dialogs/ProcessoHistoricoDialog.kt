package com.example.advogo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.adapters.ProcessosStatusAndamentosAdapter
import com.example.advogo.adapters.ProcessosTiposAndamentosAdapter
import com.example.advogo.databinding.DialogProcessoHistoricoBinding
import com.example.advogo.databinding.DialogListBinding
import com.example.advogo.databinding.DialogProcessoAndamentoBinding
import com.example.advogo.databinding.DialogProcessoAnexoBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.IProcessoStatusAndamentoRepository
import com.example.advogo.repositories.IProcessoTipoAndamentoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

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