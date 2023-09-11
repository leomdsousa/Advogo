package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.databinding.ActivityDiligenciaTiposBinding
import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.repositories.IDiligenciaTipoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciaTiposActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaTiposBinding
    @Inject lateinit var repository: IDiligenciaTipoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiligenciaTiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Diligencias Tipos", binding.toolbarDiligenciaTipos)

        repository.obterDiligenciasTipos(
            { lista ->
                setTiposToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun setTiposToUI(lista: List<DiligenciaTipo>) {
        if(lista.isNotEmpty()) {
            binding.rvTiposList.visibility = View.VISIBLE
            binding.tvNoTipoAvailable.visibility = View.GONE

            binding.rvTiposList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvTiposList.setHasFixedSize(true)

            val adapter = DiligenciasTiposAdapter(
                this,
                lista
            )
            binding.rvTiposList.adapter = adapter

            adapter.setOnItemClickListener(object :
                DiligenciasTiposAdapter.OnItemClickListener {
                override fun onClick(item: DiligenciaTipo, position: Int, action: String) {
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