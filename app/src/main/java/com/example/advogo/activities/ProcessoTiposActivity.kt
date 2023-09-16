package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.ActivityProcessoTiposBinding
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.dialogs.form.ProcessoTipoDialog
import com.example.advogo.models.ProcessoTipo
import com.example.advogo.repositories.IProcessoTipoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoTiposActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoTiposBinding
    private lateinit var bindingDialog: DialogListFormBinding
    @Inject lateinit var repository: IProcessoTipoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoTiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Processos Tipos", binding.toolbarProcessoTipos)

        binding.fabAddProcessoTipo.setOnClickListener {
            openDialog(null)
        }

        obterProcessosTipos()
    }

    private fun obterProcessosTipos() {
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
                lista,
                false
            )
            binding.rvTiposList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosTiposAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoTipo, position: Int, action: String) {
                    openDialog(item)
                }
                override fun onEdit(item: ProcessoTipo, position: Int) {
                    openDialog(item)
                }
                override fun onDelete(item: ProcessoTipo, position: Int) {
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

    private fun openDialog(processoTipo: ProcessoTipo? = null) {
        bindingDialog = DialogListFormBinding.inflate(layoutInflater)

        val dialog = object : ProcessoTipoDialog(
            this,
            processoTipo ?: ProcessoTipo(),
            bindingDialog
        ) {
            override fun onSubmit(processoTipo: ProcessoTipo) {
                if(processoTipo.id.isBlank()) {
                    adicionar(processoTipo)
                } else {
                    atualizar(processoTipo)
                }
            }
        }

        dialog.show()
    }

    private fun adicionar(processoTipo: ProcessoTipo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoTipo(
                id = "",
                tipo = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.adicionarProcessoTipo(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun validarFormulario(): Boolean {
        return true
    }

    private fun atualizar(processoTipo: ProcessoTipo) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoTipo(
                id = processoTipo.id,
                tipo = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.atualizarProcessoTipo(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun deletar(item: ProcessoTipo) {
        repository.deletarProcessoTipo(
            item.id,
            { deletarSuccess() },
            { deletarFailure() }
        )
    }

    private fun alertDialogDeletar(item: ProcessoTipo) {
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
        obterProcessosTipos()
    }

    private fun deletarFailure() {
        hideProgressDialog()
    }

    private fun saveSuccess() {
        obterProcessosTipos()
    }

    private fun saveFailure() {
        hideProgressDialog()
    }
}