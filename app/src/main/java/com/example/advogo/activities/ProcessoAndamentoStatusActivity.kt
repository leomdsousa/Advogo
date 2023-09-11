package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosStatusAndamentosAdapter
import com.example.advogo.databinding.ActivityProcessoAndamentoStatusBinding
import com.example.advogo.models.ProcessoStatusAndamento
import com.example.advogo.repositories.IProcessoStatusAndamentoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAndamentoStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoAndamentoStatusBinding
    @Inject lateinit var repository: IProcessoStatusAndamentoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoAndamentoStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Andamentos Status", binding.toolbarProcessoAndamentoStatus)

        repository.obterProcessoStatusAndamentos(
            { lista ->
                setStatusToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setStatusToUI(lista: List<ProcessoStatusAndamento>) {
        if(lista.isNotEmpty()) {
            binding.rvStatusList.visibility = View.VISIBLE
            binding.tvNoStatusAvailable.visibility = View.GONE

            binding.rvStatusList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvStatusList.setHasFixedSize(true)

            val adapter = ProcessosStatusAndamentosAdapter(
                this,
                lista
            )
            binding.rvStatusList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosStatusAndamentosAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoStatusAndamento, position: Int, action: String) {
                    //TODO - Abrir Dialog de inclusão ou atualização
                }
            })
        } else {
            binding.rvStatusList.visibility = View.GONE
            binding.tvNoStatusAvailable.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}