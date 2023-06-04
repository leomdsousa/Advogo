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
import com.example.advogo.activities.ClienteDetalheActivity
import com.example.advogo.adapters.AnexosAdapter
import com.example.advogo.adapters.ClientesAdapter
import com.example.advogo.databinding.FragmentProcessoAnexoBinding
import com.example.advogo.databinding.FragmentProcessoDetalheBinding
import com.example.advogo.models.Anexo
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IAnexoRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAnexoFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoAnexoBinding
    @Inject lateinit var anexoRepository: IAnexoRepository

    private lateinit var processoDetalhes: Processo
    //private var anexos: List<Anexo> = emptyList()

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

        if(processoDetalhes.anexosLista?.isNotEmpty() == true) {
            setAnexosToUI(processoDetalhes.anexosLista as ArrayList<Anexo>)
        }

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

    private fun obterIntentDados() {
        if (requireActivity().intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = requireActivity().intent.getParcelableExtra<Processo>(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun setAnexosToUI(lista: ArrayList<Anexo>) {
        //TODO("hideProgressDialog()")

        if(lista.size > 0) {
            binding.rvAnexosLista.visibility = View.VISIBLE
            binding.tvNenhumAnexoDisponivel.visibility = View.GONE

            binding.rvAnexosLista.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvAnexosLista.setHasFixedSize(true)

            val adapter = AnexosAdapter(binding.root.context, lista)
            binding.rvAnexosLista.adapter = adapter

            adapter.setOnItemClickListener(object :
                AnexosAdapter.OnItemClickListener {
                override fun onView(anexo: Anexo, position: Int) {
                    if(!TextUtils.isEmpty(anexo.uri)) {
                        abrirArquivo(anexo.uri!!)
                    }

                }
                override fun onDelete(anexo: Anexo, position: Int) {
//                    val intent = Intent(binding.root.context, ClienteDetalheActivity::class.java)
//                    intent.putExtra(Constants.CLIENTE_PARAM, anexo)
//                    startActivity(intent)
                }
            })

        } else {
            binding.rvAnexosLista.visibility = View.GONE
            binding.tvNenhumAnexoDisponivel.visibility = View.VISIBLE
        }
    }

//    fun onAnexosAtualizados(anexos: List<Anexo>) {
//        this.anexos = anexos
//    }
}

//interface AnexoCallback {
//    fun onAnexosAtualizados(anexos: List<Anexo>)
//}