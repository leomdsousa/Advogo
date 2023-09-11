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
import com.example.advogo.adapters.*
import com.example.advogo.databinding.DialogProcessoAndamentoBinding
import com.example.advogo.databinding.FragmentProcessoAndamentoBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.IProcessoAndamentoRepository
import com.example.advogo.repositories.IProcessoStatusAndamentoRepository
import com.example.advogo.repositories.IProcessoTipoAndamentoRepository
import com.example.advogo.utils.constants.Constants
import com.example.advogo.dialogs.form.ProcessoAndamentoDialog
import com.example.advogo.utils.UserUtils.getCurrentUserID
import com.example.advogo.utils.extensions.DataUtils
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessoAndamentoFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoAndamentoBinding
    private lateinit var bindingDialog: DialogProcessoAndamentoBinding

    @Inject lateinit var processoAndamentoRepository: IProcessoAndamentoRepository
    @Inject lateinit var tipoAndamentoRepository: IProcessoTipoAndamentoRepository
    @Inject lateinit var statusAndamentoRepository: IProcessoStatusAndamentoRepository
    private lateinit var processoDetalhes: Processo
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var tiposAndamentos: List<ProcessoTipoAndamento> = emptyList()
    private var statusAndamentos: List<ProcessoStatusAndamento> = emptyList()

    private var dataSelecionada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessoAndamentoBinding.inflate(inflater, container, false)

        CoroutineScope(Dispatchers.Main).launch {
            tiposAndamentos = tipoAndamentoRepository.obterProcessoTipoAndamentos() ?: emptyList()
            statusAndamentos = statusAndamentoRepository.obterProcessoStatusAndamentos() ?: emptyList()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        setProcessoAndamentosToUI(processoDetalhes.andamentosLista)

        binding.fabAddAndamento.setOnClickListener {
            andamentoProcessoDialog(null)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANDAMENTOS_ACTIVITY)) {
                    processoAndamentoRepository.obterProcessosAndamentos(
                        { lista -> setProcessoAndamentosToUI(lista as ArrayList<ProcessoAndamento>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    private fun obterIntentDados() {
        if (requireActivity().intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = requireActivity().intent.getParcelableExtra<Processo>(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun setProcessoAndamentosToUI(lista: List<ProcessoAndamento>?) {
        if (lista != null && lista.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                if(lista.isNotEmpty()) {
                    binding.rvAndamentosLista.visibility = View.VISIBLE
                    binding.tvNenhumAndamentoDisponivel.visibility = View.GONE

                    binding.rvAndamentosLista.layoutManager = LinearLayoutManager(binding.root.context)
                    binding.rvAndamentosLista.setHasFixedSize(true)

                    val adapter = ProcessosAndamentosAdapter(binding.root.context, lista)
                    binding.rvAndamentosLista.adapter = adapter

                    adapter.setOnItemClickListener(object :
                        ProcessosAndamentosAdapter.OnItemClickListener {
                        override fun onClick(andamento: ProcessoAndamento, position: Int) {
                            andamentoProcessoDialog(andamento)
                        }
                    })
                } else {
                    binding.rvAndamentosLista.visibility = View.GONE
                    binding.tvNenhumAndamentoDisponivel.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun andamentoProcessoDialog(andamento: ProcessoAndamento? = null) {
        bindingDialog = DialogProcessoAndamentoBinding.inflate(layoutInflater)

        val dialog = object : ProcessoAndamentoDialog(
            requireContext(),
            andamento ?: ProcessoAndamento(),
            bindingDialog,
            tiposAndamentos,
            statusAndamentos,
            andamento != null
        ) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSubmit(andamento: ProcessoAndamento) {
                if(andamento.id.isBlank()) {
                    adicionarAndamento(andamento)
                } else {
                    atualizarAndamento(andamento)
                }
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChooseDate() {
                showDataPicker(requireContext()) { ano, mes, dia ->
                    onDatePickerResult(ano, mes, dia)
                }
            }
        }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun atualizarAndamento(andamento: ProcessoAndamento) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val input = ProcessoAndamento(
            id = andamento.id,
            descricao = bindingDialog.etDescricaoAndamento.text.toString(),
            advogado = getCurrentUserID(),
            processo = processoDetalhes.numero,
            tipo = bindingDialog.etTipoAndamentoProcesso.text.toString(),
            status = bindingDialog.etStatusAndamentoProcesso.text.toString(),
//            tipo = (bindingDialog.spinnerTipoAndamentoProcesso.selectedItem as ProcessoTipoAndamento)?.id,
//            status = (bindingDialog.spinnerStatusProcessoAndamento .selectedItem as ProcessoStatusAndamento)?.id,
            data = dataSelecionada,
        )

        input.dataTimestamp = Timestamp(input.data!!.fromUSADateStringToDate())

//        if(bindingDialog.etDataAndamento.text.toString().isNotEmpty()) {
//            val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//            val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
//            val fromDate = fromFormat.parse(bindingDialog.etDataAndamento.text.toString())
//            val selectedDate = toFormat.format(fromDate)
//            input.data = selectedDate
//        }

        processoAndamentoRepository.atualizarProcessoAndamento(
            input,
            { saveAndamentoSuccess() },
            { saveAndamentoFailure() }
        )
    }

    private fun adicionarAndamento(andamento: ProcessoAndamento) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val input = ProcessoAndamento(
            id = andamento.id,
            descricao = bindingDialog.etDescricaoAndamento.text.toString(),
            advogado = getCurrentUserID(),
            processo = processoDetalhes.numero,
            tipo = bindingDialog.etTipoAndamentoProcesso.text.toString(),
            status = bindingDialog.etStatusAndamentoProcesso.text.toString(),
//            tipo = (bindingDialog.spinnerTipoAndamentoProcesso.selectedItem as ProcessoTipoAndamento)?.id,
//            status = (bindingDialog.spinnerStatusProcessoAndamento .selectedItem as ProcessoStatusAndamento)?.id,
            data = dataSelecionada,
            dataTimestamp = Timestamp.now()
        )

//        if(bindingDialog.etDataAndamento.text.toString().isNotEmpty()) {
//            val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//            val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
//            val fromDate = fromFormat.parse(bindingDialog.etDataAndamento.text.toString())
//            val selectedDate = toFormat.format(fromDate)
//            input.data = selectedDate
//        }

        processoAndamentoRepository.adicionarProcessoAndamento(
            input,
            { saveAndamentoSuccess() },
            { saveAndamentoFailure() }
        )
    }

    private fun saveAndamentoSuccess() {
        processoAndamentoRepository.obterProcessosAndamentosPorProcesso(
            processoDetalhes.numero!!,
            {
                setProcessoAndamentosToUI(it as ArrayList<ProcessoAndamento>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun saveAndamentoFailure() {
        hideProgressDialog()

        Toast.makeText(
            requireContext(),
            "Erro para salvar o andamento!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(bindingDialog.etDescricaoAndamento .text.toString())) {
            bindingDialog.etDescricaoAndamento.error = "Obrigatório"
            bindingDialog.etDescricaoAndamento.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(bindingDialog.etDataAndamento.text.toString())) {
            bindingDialog.etDataAndamento.error = "Obrigatório"
            bindingDialog.etDataAndamento.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(bindingDialog.etTipoAndamentoProcesso.text.toString())) {
            bindingDialog.etTipoAndamentoProcesso.error = "Obrigatório"
            bindingDialog.etTipoAndamentoProcesso.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(bindingDialog.etStatusAndamentoProcesso.text.toString())) {
            bindingDialog.etStatusAndamentoProcesso.error = "Obrigatório"
            bindingDialog.etStatusAndamentoProcesso.requestFocus()
            validado = false
        }

//        if (bindingDialog.spinnerTipoAndamentoProcesso.selectedItem == null) {
//            validado = false
//        }
//
//        if (bindingDialog.spinnerStatusProcessoAndamento.selectedItem == null) {
//            validado = false
//        }

        return validado
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDatePickerResult(ano: Int, mes: Int, dia: Int) {
        val retorno = DataUtils.onDatePickerResult(ano, mes, dia)

        dataSelecionada = retorno.dataUSA
        bindingDialog.etDataAndamento.setText(retorno.dataBR)
    }
}