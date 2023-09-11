package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.AdvogadosAdapter
import com.example.advogo.adapters.ProcessosStatusAdapter
import com.example.advogo.databinding.ActivityAdvogadoBinding
import com.example.advogo.databinding.ActivityProcessoStatusBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.ProcessoStatus
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.repositories.IProcessoStatusRepository
import com.example.advogo.utils.constants.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoStatusBinding
    @Inject lateinit var repository: IProcessoStatusRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Processos Status", binding.toolbarProcessoStatus)

        repository.obterProcessosStatus(
            { lista ->
                setStatusToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setStatusToUI(lista: List<ProcessoStatus>) {
        if(lista.isNotEmpty()) {
            binding.rvStatusList.visibility = View.VISIBLE
            binding.tvNoStatusAvailable.visibility = View.GONE

            binding.rvStatusList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvStatusList.setHasFixedSize(true)

            val adapter = ProcessosStatusAdapter(
                this,
                lista
            )
            binding.rvStatusList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosStatusAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoStatus, position: Int, action: String) {
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