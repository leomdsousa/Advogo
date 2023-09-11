package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.databinding.ActivityDiligenciaStatusBinding
import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.repositories.IDiligenciaStatusRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciaStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaStatusBinding
    @Inject lateinit var repository: IDiligenciaStatusRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiligenciaStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Diligencias Status", binding.toolbarDiligenciaStatus)

        repository.obterDiligenciasStatus(
            { lista ->
                setStatusToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setStatusToUI(lista: List<DiligenciaStatus>) {
        if(lista.isNotEmpty()) {
            binding.rvStatusList.visibility = View.VISIBLE
            binding.tvNoStatusAvailable.visibility = View.GONE

            binding.rvStatusList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvStatusList.setHasFixedSize(true)

            val adapter = DiligenciasStatusAdapter(
                this,
                lista
            )
            binding.rvStatusList.adapter = adapter

            adapter.setOnItemClickListener(object :
                DiligenciasStatusAdapter.OnItemClickListener {
                override fun onClick(item: DiligenciaStatus, position: Int, action: String) {
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