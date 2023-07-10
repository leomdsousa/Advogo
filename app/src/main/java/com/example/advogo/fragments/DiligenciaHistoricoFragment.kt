package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasHistoricosAdapter
import com.example.advogo.databinding.DialogDiligenciaHistoricoBinding
import com.example.advogo.databinding.FragmentDiligenciaHistoricoBinding
import com.example.advogo.dialogs.DiligenciaHistoricoDialog
import com.example.advogo.models.Diligencia
import com.example.advogo.models.DiligenciaHistorico
import com.example.advogo.repositories.IDiligenciaHistoricoRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciaHistoricoFragment : BaseFragment() {
    private lateinit var binding: FragmentDiligenciaHistoricoBinding
    private lateinit var bindingDialog: DialogDiligenciaHistoricoBinding
    @Inject lateinit var diligenciaHistoricoRepository: IDiligenciaHistoricoRepository
    private lateinit var diligenciaDetalhes: Diligencia
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiligenciaHistoricoBinding.inflate(inflater, container, false)
        return binding.root    
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        binding.fabDiligenciaHistoricoCadastro.setOnClickListener {
            diligenciaHistoricoDialog(null)
        }

        if(diligenciaDetalhes.historico?.isNotEmpty() == true) {
            setDiligenciaHistoricoToUI(diligenciaDetalhes.historicoLista as ArrayList<DiligenciaHistorico>)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANEXOS_ACTIVITY)) {
                    diligenciaHistoricoRepository.obterDiligenciasHistoricos(
                        { lista -> setDiligenciaHistoricoToUI(lista as ArrayList<DiligenciaHistorico>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    private fun setDiligenciaHistoricoToUI(lista: ArrayList<DiligenciaHistorico>) {
        CoroutineScope(Dispatchers.Main).launch {
            if(lista.size > 0) {
                binding.rvDiligenciaHistoricoList .visibility = View.VISIBLE
                binding.tvNoDiligenciaHistoricoAvailable.visibility = View.GONE

                binding.rvDiligenciaHistoricoList.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvDiligenciaHistoricoList.setHasFixedSize(true)

                val adapter = DiligenciasHistoricosAdapter(binding.root.context, lista)
                binding.rvDiligenciaHistoricoList.adapter = adapter

                adapter.notifyItemChanged(1, null)

                adapter.setOnItemClickListener(object :
                    DiligenciasHistoricosAdapter.OnItemClickListener {
                    override fun onClick(historico: DiligenciaHistorico, position: Int) {
                        diligenciaHistoricoDialog(historico)
                    }
                })

            } else {
                binding.rvDiligenciaHistoricoList.visibility = View.GONE
                binding.tvNoDiligenciaHistoricoAvailable.visibility = View.VISIBLE
            }
        }
    }

    private fun obterIntentDados() {
        if (requireActivity().intent.hasExtra(Constants.DILIGENCIA_PARAM)) {
            diligenciaDetalhes = requireActivity().intent.getParcelableExtra(Constants.DILIGENCIA_PARAM)!!
        }
    }

    private fun diligenciaHistoricoDialog(historico: DiligenciaHistorico? = null) {
        val dialog = object : DiligenciaHistoricoDialog(
            requireContext(),
            historico ?: DiligenciaHistorico()
        ) {
            override fun onSubmit(historico: DiligenciaHistorico) {
                if(historico.id.isBlank()) {
                    adicionarHistorico(historico)
                } else {
                    atualizarHistorico(historico)
                }
            }
        }

        dialog.show()

        bindingDialog = DialogDiligenciaHistoricoBinding.inflate(dialog.layoutInflater)
    }

    private fun atualizarHistorico(historico: DiligenciaHistorico) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val input = DiligenciaHistorico(
            id = historico.id,
            obs = bindingDialog.etDescricaoHistorico.text.toString(),
            advogado = diligenciaDetalhes.advogado,
            status = diligenciaDetalhes.status,
            tipo = diligenciaDetalhes.tipo,
            data = null
        )

        diligenciaHistoricoRepository.atualizarDiligenciaHistorico(
            input,
            { saveHistoricoSuccess() },
            { saveHistoricoFailure() }
        )
    }

    private fun adicionarHistorico(historico: DiligenciaHistorico) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val input = DiligenciaHistorico(
            id = "",
            obs = bindingDialog.etDescricaoHistorico.text.toString(),
            advogado = diligenciaDetalhes.advogado,
            status = diligenciaDetalhes.status,
            tipo = diligenciaDetalhes.tipo,
            data = null
        )

        diligenciaHistoricoRepository.adicionarDiligenciaHistorico(
            input,
            { saveHistoricoSuccess() },
            { saveHistoricoFailure() }
        )
    }

    private fun saveHistoricoSuccess() {
        diligenciaHistoricoRepository.obterDiligenciasHistoricos(
            {
                setDiligenciaHistoricoToUI(it as ArrayList<DiligenciaHistorico>)
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