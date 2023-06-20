package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.activities.ClienteDetalheActivity
import com.example.advogo.adapters.AnexosAdapter
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.databinding.DialogProcessoAndamentoBinding
import com.example.advogo.databinding.DialogProcessoAnexoBinding
import com.example.advogo.databinding.FragmentProcessoAnexoBinding
import com.example.advogo.databinding.FragmentProcessoDetalheBinding
import com.example.advogo.models.Anexo
import com.example.advogo.models.Processo
import com.example.advogo.models.ProcessoAndamento
import com.example.advogo.repositories.IAnexoRepository
import com.example.advogo.utils.Constants
import com.example.projmgr.dialogs.ProcessoAndamentoDialog
import com.example.projmgr.dialogs.ProcessoAnexoDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAnexoFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoAnexoBinding
    private lateinit var bindingDialog: DialogProcessoAnexoBinding
    @Inject lateinit var anexoRepository: IAnexoRepository
    private lateinit var processoDetalhes: Processo
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessoAnexoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        setAnexosToUI(processoDetalhes.anexosLista as ArrayList<Anexo>?)

        binding.fabAddAnexo.setOnClickListener {
            anexoProcessoDialog(null)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANEXOS_ACTIVITY)) {
                    anexoRepository.ObterAnexos(
                        { lista -> setAnexosToUI(lista as ArrayList<Anexo>) },
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
            processoDetalhes = requireActivity().intent.getParcelableExtra(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun setAnexosToUI(lista: ArrayList<Anexo>?) {
        if (lista != null) {
            if(lista.size > 0) {
                binding.rvAnexosLista.visibility = View.VISIBLE
                binding.tvNenhumAnexoDisponivel.visibility = View.GONE

                binding.rvAnexosLista.layoutManager = LinearLayoutManager(binding.root.context)
                binding.rvAnexosLista.setHasFixedSize(true)

                val adapter = AnexosAdapter(binding.root.context, lista)
                binding.rvAnexosLista.adapter = adapter

                adapter.setOnItemClickListener(object :
                    AnexosAdapter.OnItemClickListener {
                    override fun onClick(anexo: Anexo) {
                        anexoProcessoDialog(anexo)
                    }
                    override fun onView(anexo: Anexo, position: Int) {
                        if(!TextUtils.isEmpty(anexo.uri)) {
                            abrirArquivo(anexo.uri!!)
                        }

                    }
                    override fun onDelete(anexo: Anexo, position: Int) {
                        if(!TextUtils.isEmpty(anexo.uri)) {
                            deletarArquivo(anexo.uri!!)
                        }
                    }
                })

            } else {
                binding.rvAnexosLista.visibility = View.GONE
                binding.tvNenhumAnexoDisponivel.visibility = View.VISIBLE
            }
        }
    }

    private fun anexoProcessoDialog(anexo: Anexo? = null) {
        val dialog = object : ProcessoAnexoDialog(
            requireContext(),
            anexo ?: Anexo()
        ) {
            override fun onSubmit(anexo: Anexo) {
                if(anexo.id.isBlank()) {
                    adicionarAnexo(anexo)
                } else {
                    atualizarAnexo(anexo)
                }
            }
        }

        dialog.show()

        bindingDialog = DialogProcessoAnexoBinding.inflate(dialog.layoutInflater)
    }

    private fun atualizarAnexo(anexo: Anexo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val anexo = Anexo(
            id = processoDetalhes.id,
            descricao = bindingDialog.etDescricaoAnexo.text.toString(),
            nome = bindingDialog.tvTitle.text.toString(),
            //uri = uri,
        )

        anexoRepository.AtualizarAnexo(
            anexo,
            { saveAnexoSuccess() },
            { saveAnexoFailure() }
        )
    }

    private fun adicionarAnexo(anexo: Anexo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val anexo = Anexo(
            id = processoDetalhes.id,
            descricao = bindingDialog.etDescricaoAnexo.text.toString(),
            nome = bindingDialog.tvTitle.text.toString(),
            //uri = uri,
        )

        anexoRepository.AdicionarAnexo(
            anexo,
            { saveAnexoSuccess() },
            { saveAnexoFailure() }
        )
    }

    private fun saveAnexoSuccess() {
        anexoRepository.ObterAnexos(
            {
                setAnexosToUI(it as ArrayList<Anexo>)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun saveAnexoFailure() {
        hideProgressDialog()

        Toast.makeText(
            requireContext(),
            "Erro para salvar o anexo!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(bindingDialog.etDescricaoAnexo.text.toString())) {
            bindingDialog.etDescricaoAnexo.error = "Obrigatório"
            bindingDialog.etDescricaoAnexo.requestFocus()
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