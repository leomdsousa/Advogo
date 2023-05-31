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
import com.example.advogo.activities.DiligenciaCadastroActivity
import com.example.advogo.databinding.FragmentDiligenciasBinding
import com.example.advogo.databinding.FragmentProcessoAnexoBinding
import com.example.advogo.models.Anexo
import com.example.advogo.models.Diligencia
import com.example.advogo.repositories.IAnexoRepository
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAnexoFragment : Fragment() {
    private lateinit var binding: FragmentProcessoAnexoBinding
    @Inject lateinit var anexoRepository: IAnexoRepository

    private var anexos: List<Anexo> = emptyList()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_processo_anexo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    fun onAnexosAtualizados(anexos: List<Anexo>) {
        this.anexos = anexos
    }
}