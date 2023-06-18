package com.example.projmgr.dialogs

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

abstract class ProcessoAnexoDialog(
    context: Context,
    private var anexo: Anexo
): Dialog(context) {
    private lateinit var binding: DialogProcessoAnexoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        binding = DialogProcessoAnexoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setDados(anexo)

        binding.btnSubmitProcessoAnexo.setOnClickListener {
            dismiss()
            onSubmit(anexo)
        }
    }

    private fun setDados(anexo: Anexo) {
        if(anexo.id.isBlank()) {
            binding.tvTitle.text = "Cadastro Andamento"
            binding.btnSubmitProcessoAnexo.text = "Cadastrar"
        } else {
            binding.tvTitle.text = "Detalhes Andamento"
            binding.btnSubmitProcessoAnexo.text = "Atualizar"

            binding.etDescricaoAnexo.setText(anexo.descricao)
        }
    }

    protected abstract fun onSubmit(anexo: Anexo)
}