package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.ActivityProcessoTiposBinding
import com.example.advogo.models.ProcessoTipo
import com.example.advogo.repositories.IProcessoTipoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoTiposActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoTiposBinding
    @Inject lateinit var repository: IProcessoTipoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoTiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Processos Tipos", binding.toolbarProcessoTipos)

        repository.obterProcessosTipos(
            { lista ->
                setTiposToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setTiposToUI(lista: List<ProcessoTipo>) {
        if(lista.isNotEmpty()) {
            binding.rvTiposList.visibility = View.VISIBLE
            binding.tvNoTipoAvailable.visibility = View.GONE

            binding.rvTiposList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvTiposList.setHasFixedSize(true)

            val adapter = ProcessosTiposAdapter(
                this,
                lista
            )
            binding.rvTiposList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosTiposAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoTipo, position: Int, action: String) {
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