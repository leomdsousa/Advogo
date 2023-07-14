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
import com.example.advogo.databinding.DialogDiligenciaHistoricoBinding
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
    private var historico: DiligenciaHistorico
): Dialog(context) {
    private lateinit var binding: DialogDiligenciaHistoricoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogDiligenciaHistoricoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(historico)

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