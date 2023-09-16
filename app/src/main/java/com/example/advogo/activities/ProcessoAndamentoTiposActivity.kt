package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosTiposAndamentosAdapter
import com.example.advogo.databinding.ActivityProcessoAndamentoTiposBinding
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.dialogs.form.ProcessoTipoAndamentoDialog
import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.repositories.IProcessoTipoAndamentoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAndamentoTiposActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoAndamentoTiposBinding
    private lateinit var bindingDialog: DialogListFormBinding
    @Inject lateinit var repository: IProcessoTipoAndamentoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoAndamentoTiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Andamentos Tipos", binding.toolbarProcessoAndamentoTipos)

        binding.fabAddProcessoAndamentoTipo.setOnClickListener {
            openDialog(null)
        }

        obterProcessoAndamentosTipos()
    }

    private fun setTiposToUI(lista: List<ProcessoTipoAndamento>) {
        if(lista.isNotEmpty()) {
            binding.rvTiposList.visibility = View.VISIBLE
            binding.tvNoTipoAvailable.visibility = View.GONE

            binding.rvTiposList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvTiposList.setHasFixedSize(true)

            val adapter = ProcessosTiposAndamentosAdapter(
                this,
                lista,
                false
            )
            binding.rvTiposList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosTiposAndamentosAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoTipoAndamento, position: Int, action: String) {
                    openDialog(item)
                }

                override fun onEdit(item: ProcessoTipoAndamento, position: Int) {
                    openDialog(item)
                }

                override fun onDelete(item: ProcessoTipoAndamento, position: Int) {
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
    
    private fun obterProcessoAndamentosTipos() {
        repository.obterProcessoTipoAndamentos(
            { lista ->
                setTiposToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun openDialog(status: ProcessoTipoAndamento? = null) {
        bindingDialog = DialogListFormBinding.inflate(layoutInflater)

        val dialog = object : ProcessoTipoAndamentoDialog(
            this,
            status ?: ProcessoTipoAndamento(),
            bindingDialog
        ) {
            override fun onSubmit(processoTipoAndamento: ProcessoTipoAndamento) {
                if(processoTipoAndamento.id.isBlank()) {
                    adicionar(processoTipoAndamento)
                } else {
                    atualizar(processoTipoAndamento)
                }
            }
        }

        dialog.show()
    }

    private fun adicionar(status: ProcessoTipoAndamento) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoTipoAndamento(
                id = "",
                tipo = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.adicionarProcessoTipoAndamento(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun validarFormulario(): Boolean {
        return true
    }

    private fun atualizar(status: ProcessoTipoAndamento) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoTipoAndamento(
                id = status.id,
                tipo = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.atualizarProcessoTipoAndamento(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun deletar(item: ProcessoTipoAndamento) {
        repository.deletarProcessoTipoAndamento (
            item.id,
            { deletarSuccess() },
            { deletarFailure() }
        )
    }

    private fun alertDialogDeletar(item: ProcessoTipoAndamento) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarStatus,
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
        obterProcessoAndamentosTipos()
    }

    private fun deletarFailure() {
        hideProgressDialog()
    }

    private fun saveSuccess() {
        obterProcessoAndamentosTipos()
    }

    private fun saveFailure() {
        hideProgressDialog()
    }
}