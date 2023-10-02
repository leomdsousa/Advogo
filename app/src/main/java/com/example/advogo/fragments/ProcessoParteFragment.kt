package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.example.advogo.adapters.ProcessosPartesAdapter
import com.example.advogo.databinding.DialogProcessoParteBinding
import com.example.advogo.databinding.FragmentProcessoParteBinding
import com.example.advogo.dialogs.form.ProcessoParteDialog
import com.example.advogo.models.*
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.repositories.ITipoParteRepository
import com.example.advogo.utils.constants.Constants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoParteFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoParteBinding
    private lateinit var bindingDialog: DialogProcessoParteBinding
    @Inject lateinit var processoRepository: IProcessoRepository
    @Inject lateinit var tiposPartesRepository: ITipoParteRepository
    private lateinit var processoDetalhes: Processo
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var tiposPartes: List<TiposPartes> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        binding.fabAddProcessoParte.setOnClickListener {
            processoParteDialog(null)
        }

        setProcessoParteToUI(processoDetalhes.partes)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANEXOS_ACTIVITY)) {
                    processoRepository.obterProcesso(
                        processoDetalhes.id,
                        { processo -> setProcessoParteToUI(processo.partes) },
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
        binding = FragmentProcessoParteBinding.inflate(inflater, container, false)

        CoroutineScope(Dispatchers.Main).launch {
            tiposPartes = tiposPartesRepository.obterProcessosTipos() ?: emptyList()
        }

        return binding.root
    }

    private fun setProcessoParteToUI(lista: List<ProcessoParte>) {
        CoroutineScope(Dispatchers.Main).launch {
            if(lista.isNotEmpty()) {
                binding.rvProcessoParteLista.visibility = View.VISIBLE
                binding.tvNenhumProcessoParteDisponivel.visibility = View.GONE

                binding.rvProcessoParteLista.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvProcessoParteLista.setHasFixedSize(true)

                val adapter = ProcessosPartesAdapter(binding.root.context, lista)
                binding.rvProcessoParteLista.adapter = adapter

                adapter.notifyItemChanged(1, null)

                adapter.setOnItemClickListener(object :
                    ProcessosPartesAdapter.OnItemClickListener {
                    override fun onClick(parte: ProcessoParte, position: Int) {
                        processoParteDialog(parte)
                    }
                })

            } else {
                binding.rvProcessoParteLista.visibility = View.GONE
                binding.tvNenhumProcessoParteDisponivel.visibility = View.VISIBLE
            }
        }
    }

    private fun obterIntentDados() {
        if (requireActivity().intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = requireActivity().intent.getParcelableExtra(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun processoParteDialog(parte: ProcessoParte? = null) {
        bindingDialog = DialogProcessoParteBinding.inflate(layoutInflater)

        val dialog = object : ProcessoParteDialog(
            requireContext(),
            parte ?: ProcessoParte(),
            bindingDialog,
            tiposPartes,
            parte != null
        ) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSubmit(parte: ProcessoParte) {
                setPartes(parte)
            }
        }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPartes(parte: ProcessoParte) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val inputParte = ProcessoParte(
            nome = bindingDialog.etNomeParte.text.toString(),
            documento = bindingDialog.etDocumentoParte.text.toString(),
            contato = bindingDialog.etContatoParte.text.toString(),
            tipo = bindingDialog.tipoHidden.text.toString(),
        )

        if(processoDetalhes.partes.isEmpty()) {
            processoDetalhes.partes = emptyList()
            processoDetalhes.partes = processoDetalhes.partes.plus(inputParte)
        } else {
            processoDetalhes.partes = processoDetalhes.partes.plus(inputParte)
        }

        processoDetalhes.dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        processoDetalhes.dataAlteracaoTimestamp = Timestamp.now()

        //TODO - Provisório até resolver a questão de fazer o Transient e/ou Exclude dessas propriedades funcionarem
        processoDetalhes.tipoObj = null
        processoDetalhes.statusObj = null
        processoDetalhes.clienteObj = null
        processoDetalhes.advogadoObj = null
        processoDetalhes.diligenciasLista = null
        processoDetalhes.anexosLista = null
        processoDetalhes.andamentosLista = null
        processoDetalhes.historicoLista = null
        processoDetalhes.selecionado = null
        processoDetalhes.imagemSelecionadaURI = null

        processoRepository.atualizarProcesso(
            processoDetalhes,
            { saveParteSuccess() },
            { saveParteFailure() }
        )
    }

    private fun saveParteSuccess() {
        processoRepository.obterProcessoPorNumero(
            processoDetalhes.numero!!,
            {
                setProcessoParteToUI(it.partes)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun saveParteFailure() {
        hideProgressDialog()

        Toast.makeText(
            requireContext(),
            "Erro para salvar a parte!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(bindingDialog.etNomeParte.text.toString())) {
            bindingDialog.etNomeParte.error = "Obrigatório"
            bindingDialog.etNomeParte.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(bindingDialog.etTipoParte.text.toString())) {
            bindingDialog.etTipoParte.error = "Obrigatório"
            bindingDialog.etTipoParte.requestFocus()
            validado = false
        }

        return validado
    }
}