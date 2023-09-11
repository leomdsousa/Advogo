package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosHistoricosAdapter
import com.example.advogo.databinding.DialogProcessoHistoricoBinding
import com.example.advogo.databinding.FragmentProcessoHistoricoBinding
import com.example.advogo.dialogs.form.ProcessoHistoricoDialog
import com.example.advogo.models.Processo
import com.example.advogo.models.ProcessoHistorico
import com.example.advogo.repositories.IProcessoHistoricoRepository
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessoHistoricoFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoHistoricoBinding
    private lateinit var bindingDialog: DialogProcessoHistoricoBinding
    @Inject lateinit var processoHistoricoRepository: IProcessoHistoricoRepository
    private lateinit var processoDetalhes: Processo
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        binding.fabAddProcessoHistorico.setOnClickListener {
            processoHistoricoDialog(null)
        }

        setProcessoHistoricoToUI(processoDetalhes.historicoLista)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANEXOS_ACTIVITY)) {
                    processoHistoricoRepository.obterProcessosHistoricos(
                        { lista -> setProcessoHistoricoToUI(lista as ArrayList<ProcessoHistorico>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProcessoHistoricoBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setProcessoHistoricoToUI(lista: List<ProcessoHistorico>?) {
        CoroutineScope(Dispatchers.Main).launch {
            if(lista != null && lista.isNotEmpty()) {
                binding.rvProcessoHistoricoLista.visibility = View.VISIBLE
                binding.tvNenhumProcessoHistoricDisponivel.visibility = View.GONE

                binding.rvProcessoHistoricoLista.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvProcessoHistoricoLista.setHasFixedSize(true)

                val adapter = ProcessosHistoricosAdapter(binding.root.context, lista)
                binding.rvProcessoHistoricoLista.adapter = adapter

                adapter.notifyItemChanged(1, null)

                adapter.setOnItemClickListener(object :
                    ProcessosHistoricosAdapter.OnItemClickListener {
                    override fun onClick(historico: ProcessoHistorico, position: Int) {
                        processoHistoricoDialog(historico)
                    }
                })

            } else {
                binding.rvProcessoHistoricoLista.visibility = View.GONE
                binding.tvNenhumProcessoHistoricDisponivel.visibility = View.VISIBLE
            }
        }
    }

    private fun obterIntentDados() {
        if (requireActivity().intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = requireActivity().intent.getParcelableExtra(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun processoHistoricoDialog(historico: ProcessoHistorico? = null) {
        bindingDialog = DialogProcessoHistoricoBinding.inflate(layoutInflater)

        val dialog = object : ProcessoHistoricoDialog(
            requireContext(),
            historico ?: ProcessoHistorico(),
            bindingDialog,
            historico != null
        ) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSubmit(historico: ProcessoHistorico) {
                if(historico.id.isBlank()) {
                    adicionarHistorico(historico)
                } else {
                    atualizarHistorico(historico)
                }
            }
        }

        dialog.show()

        //bindingDialog = DialogProcessoHistoricoBinding.inflate(dialog.layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun atualizarHistorico(historico: ProcessoHistorico) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val input = ProcessoHistorico(
            id = historico.id,
            obs = bindingDialog.etDescricaoHistorico.text.toString(),
            advogado = processoDetalhes.advogado,
            status = processoDetalhes.status,
            tipo = processoDetalhes.tipo,
            data = historico.data,
            processo = processoDetalhes.numero
        )

        input.dataTimestamp = Timestamp(input.data!!.fromUSADateStringToDate())

        processoHistoricoRepository.atualizarProcessoHistorico(
            input,
            { saveHistoricoSuccess() },
            { saveHistoricoFailure() }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun adicionarHistorico(historico: ProcessoHistorico) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val input = ProcessoHistorico(
            id = "",
            obs = bindingDialog.etDescricaoHistorico.text.toString(),
            advogado = processoDetalhes.advogado,
            status = processoDetalhes.status,
            tipo = processoDetalhes.tipo,
            data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            dataTimestamp = Timestamp.now(),
            processo = processoDetalhes.numero
        )

        processoHistoricoRepository.adicionarProcessoHistorico(
            input,
            { saveHistoricoSuccess() },
            { saveHistoricoFailure() }
        )
    }

    private fun saveHistoricoSuccess() {
        processoHistoricoRepository.obterProcessosHistoricosPorProcesso(
            processoDetalhes.numero!!,
            {
                setProcessoHistoricoToUI(it as ArrayList<ProcessoHistorico>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun saveHistoricoFailure() {
        hideProgressDialog()

        Toast.makeText(
            requireContext(),
            "Erro para salvar o histórico!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(bindingDialog.etDescricaoHistorico.text.toString())) {
            bindingDialog.etDescricaoHistorico.error = "Obrigatório"
            bindingDialog.etDescricaoHistorico.requestFocus()
            validado = false
        }

//        if (TextUtils.isEmpty(bindingDialog.etDataAndamento.text.toString())) {
//            bindingDialog.etDataAndamento.error = "Obrigatório"
//            bindingDialog.etDataAndamento.requestFocus()
//            validado = false
//        }

        return validado
    }
}