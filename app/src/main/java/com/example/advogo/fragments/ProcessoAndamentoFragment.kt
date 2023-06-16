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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.AnexosAdapter
import com.example.advogo.adapters.ProcessosAndamentosAdapter
import com.example.advogo.databinding.FragmentProcessoAndamentoBinding
import com.example.advogo.databinding.FragmentProcessoAnexoBinding
import com.example.advogo.models.Anexo
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.models.ProcessoAndamento
import com.example.advogo.repositories.IDiligenciaHistoricoRepository
import com.example.advogo.repositories.IProcessoAndamentoRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAndamentoFragment : Fragment() {
    private lateinit var binding: FragmentProcessoAndamentoBinding
    @Inject lateinit var processoAndamentoRepository: IProcessoAndamentoRepository
    private lateinit var processoDetalhes: Processo
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProcessoAndamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        if(processoDetalhes.anexosLista?.isNotEmpty() == true) {
            setProcessoAndamentosToUI(processoDetalhes.andamentosLista as ArrayList<ProcessoAndamento>)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANDAMENTOS_ACTIVITY)) {
                    processoAndamentoRepository.ObterProcessosAndamentos(
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

    private fun setProcessoAndamentosToUI(lista: ArrayList<ProcessoAndamento>) {
        if(lista.size > 0) {
            binding.rvAndamentosLista.visibility = View.VISIBLE
            binding.tvNenhumAndamentoDisponivel.visibility = View.GONE

            binding.rvAndamentosLista.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvAndamentosLista.setHasFixedSize(true)

            val adapter = ProcessosAndamentosAdapter(binding.root.context, lista)
            binding.rvAndamentosLista.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosAndamentosAdapter.OnItemClickListener {
                override fun onClick(andamento: ProcessoAndamento, position: Int) {
                    //TODO("Abrir um modal de edição")
//                    val intent = Intent(binding.root.context, ClienteDetalheActivity::class.java)
//                    intent.putExtra(Constants.CLIENTE_PARAM, anexo)
//                    startActivity(intent)
                }
            })

        } else {
            binding.rvAndamentosLista.visibility = View.GONE
            binding.tvNenhumAndamentoDisponivel.visibility = View.VISIBLE
        }
    }
}