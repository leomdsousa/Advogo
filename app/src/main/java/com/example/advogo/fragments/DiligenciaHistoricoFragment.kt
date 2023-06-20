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
import com.example.advogo.R
import com.example.advogo.databinding.FragmentDiligenciaHistoricoBinding
import com.example.advogo.databinding.FragmentProcessoAnexoBinding
import com.example.advogo.models.Anexo
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IAnexoRepository
import com.example.advogo.repositories.IDiligenciaHistoricoRepository
import com.example.advogo.repositories.ProcessoAndamentoRepository
import dagger.hilt.android.AndroidEntryPoint
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

//        if(diligenciaDetalhes.anexosLista?.isNotEmpty() == true) {
//            setDiligenciaHistoricoToUI(diligenciaDetalhes.anexosLista as ArrayList<Anexo>)
//        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
//                if (result.data!!.hasExtra(Constants.FROM_ANEXOS_ACTIVITY)) {
//                    anexoRepository.ObterDiligencias(
//                        { lista -> setDiligenciasToUI(lista as ArrayList<Diligencia>) },
//                        { ex -> null } //TODO("Imlementar OnFailure")
//                    )
//                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    private fun setDiligenciaHistoricoToUI(arrayList: ArrayList<Anexo>) {

    }

    private fun obterIntentDados() {
        TODO("Not yet implemented")
    }
}