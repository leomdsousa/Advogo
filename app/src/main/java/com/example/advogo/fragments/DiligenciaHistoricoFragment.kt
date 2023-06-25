package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.activities.ProcessoDetalheActivity
import com.example.advogo.adapters.DiligenciasHistoricosAdapter
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.FragmentDiligenciaHistoricoBinding
import com.example.advogo.databinding.FragmentProcessoAnexoBinding
import com.example.advogo.models.Anexo
import com.example.advogo.models.Diligencia
import com.example.advogo.models.DiligenciaHistorico
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IAnexoRepository
import com.example.advogo.repositories.IDiligenciaHistoricoRepository
import com.example.advogo.repositories.ProcessoAndamentoRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciaHistoricoFragment : BaseFragment() {
    private lateinit var binding: FragmentDiligenciaHistoricoBinding
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

        if(diligenciaDetalhes.historico?.isNotEmpty() == true) {
            setDiligenciaHistoricoToUI(diligenciaDetalhes.historicoLista as ArrayList<DiligenciaHistorico>)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_ANEXOS_ACTIVITY)) {
                    diligenciaHistoricoRepository.ObterDiligenciasHistoricos(
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
                    override fun onClick(diligencia: DiligenciaHistorico, position: Int) {
//                        val intent = Intent(binding.root.context, ProcessoDetalheActivity::class.java)
//                        intent.putExtra(Constants.DILIGENCIA_PARAM, processo)
//                        startActivity(intent)
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
}