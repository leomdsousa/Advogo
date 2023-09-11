package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosTiposAndamentosAdapter
import com.example.advogo.databinding.ActivityProcessoAndamentoTiposBinding
import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.repositories.IProcessoTipoAndamentoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAndamentoTiposActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoAndamentoTiposBinding
    @Inject lateinit var repository: IProcessoTipoAndamentoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoAndamentoTiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Andamentos Tipos", binding.toolbarProcessoAndamentoTipos)

        repository.obterProcessoTipoAndamentos(
            { lista ->
                setTiposToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setTiposToUI(lista: List<ProcessoTipoAndamento>) {
        if(lista.isNotEmpty()) {
            binding.rvTiposList.visibility = View.VISIBLE
            binding.tvNoTipoAvailable.visibility = View.GONE

            binding.rvTiposList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvTiposList.setHasFixedSize(true)

            val adapter = ProcessosTiposAndamentosAdapter(
                this,
                lista
            )
            binding.rvTiposList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosTiposAndamentosAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoTipoAndamento, position: Int, action: String) {
                    //TODO - Abrir Dialog de inclusão ou atualização
                }
            })
        } else {
            binding.rvTiposList.visibility = View.GONE
            binding.tvNoTipoAvailable.visibility = View.VISIBLE
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