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
import com.example.advogo.activities.ProcessoCadastroActivity
import com.example.advogo.activities.ProcessoDetalheActivity
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.FragmentProcessosBinding
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessosFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessosBinding
    @Inject lateinit var processoRepository: IProcessoRepository

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProcessosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabProcessoCadastro.setOnClickListener {
            val intent = Intent(binding.root.context, ProcessoCadastroActivity::class.java)
            intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
            resultLauncher.launch(intent)
        }

        processoRepository.ObterProcessos(
            { processos -> setProcessosToUI(processos as ArrayList<Processo>) },
            { null } //TODO("Implementar")
        )

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_PROCESSO_ACTIVITY)) {
                    processoRepository.ObterProcessos(
                        { lista -> setProcessosToUI(lista!! as ArrayList<Processo>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }
    }

    fun setProcessosToUI(lista: ArrayList<Processo>) {
        //TODO("hideProgressDialog()")

        if(lista.size > 0) {
            binding.rvBoardsList.visibility = View.VISIBLE
            binding.tvNoBoardsAvailable.visibility = View.GONE

            binding.rvBoardsList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvBoardsList.setHasFixedSize(true)

            val adapter = ProcessosAdapter(binding.root.context, lista)
            binding.rvBoardsList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosAdapter.OnItemClickListener {
                override fun onClick(model: Processo, position: Int) {
                    val intent = Intent(binding.root.context, ProcessoDetalheActivity::class.java)
                    intent.putExtra(Constants.PROCESSO_PARAM, model)
                    startActivity(intent)
                }
            })

        } else {
            binding.rvBoardsList.visibility = View.GONE
            binding.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }
}