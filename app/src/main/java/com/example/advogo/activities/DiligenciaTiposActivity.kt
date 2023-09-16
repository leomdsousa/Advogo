package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.databinding.ActivityDiligenciaTiposBinding
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.dialogs.form.DiligenciaTipoDialog
import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.repositories.IDiligenciaTipoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciaTiposActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaTiposBinding
    private lateinit var bindingDialog: DialogListFormBinding
    @Inject lateinit var repository: IDiligenciaTipoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiligenciaTiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Diligencias Tipos", binding.toolbarDiligenciaTipos)

        binding.fabAddDiligenciaTipo.setOnClickListener {
            openDialog(null)
        }

        obterDiligenciaTipos()
    }

    private fun setTiposToUI(lista: List<DiligenciaTipo>) {
        if(lista.isNotEmpty()) {
            binding.rvTiposList.visibility = View.VISIBLE
            binding.tvNoTipoAvailable.visibility = View.GONE

            binding.rvTiposList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvTiposList.setHasFixedSize(true)

            val adapter = DiligenciasTiposAdapter(
                this,
                lista,
                false
            )
            binding.rvTiposList.adapter = adapter

            adapter.setOnItemClickListener(object :
                DiligenciasTiposAdapter.OnItemClickListener {
                override fun onClick(item: DiligenciaTipo, position: Int, action: String) {
                    openDialog(item)
                }
                override fun onEdit(item: DiligenciaTipo, position: Int) {
                    openDialog(item)
                }
                override fun onDelete(item: DiligenciaTipo, position: Int) {
                    alertDialogDeletar(item)
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
    
    private fun obterDiligenciaTipos() {
        repository.obterDiligenciasTipos(
            { lista ->
                setTiposToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun openDialog(diligenciaTipo: DiligenciaTipo? = null) {
        bindingDialog = DialogListFormBinding.inflate(layoutInflater)

        val dialog = object : DiligenciaTipoDialog(
            this,
            diligenciaTipo ?: DiligenciaTipo(),
            bindingDialog
        ) {
            override fun onSubmit(diligenciaTipo: DiligenciaTipo) {
                if(diligenciaTipo.id.isBlank()) {
                    adicionar(diligenciaTipo)
                } else {
                    atualizar(diligenciaTipo)
                }
            }
        }

        dialog.show()
    }

    private fun adicionar(diligenciaTipo: DiligenciaTipo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = DiligenciaTipo(
                id = "",
                tipo = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.adicionarDiligenciaTipo(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun validarFormulario(): Boolean {
        return true
    }

    private fun atualizar(diligenciaTipo: DiligenciaTipo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = DiligenciaTipo(
                id = diligenciaTipo.id,
                tipo = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.atualizarDiligenciaTipo(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun deletar(item: DiligenciaTipo) {
        repository.deletarDiligenciaTipo(
            item.id,
            { deletarSuccess() },
            { deletarFailure() }
        )
    }

    private fun alertDialogDeletar(item: DiligenciaTipo) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarTipo,
                item.tipo
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.sim)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deletar(item)
        }

        builder.setNegativeButton(resources.getString(R.string.nao)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deletarSuccess() {
        obterDiligenciaTipos()
    }

    private fun deletarFailure() {
        hideProgressDialog()
    }

    private fun saveSuccess() {
        obterDiligenciaTipos()
    }

    private fun saveFailure() {
        hideProgressDialog()
    }
}