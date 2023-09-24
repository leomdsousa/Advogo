package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosStatusAndamentosAdapter
import com.example.advogo.databinding.ActivityProcessoAndamentoStatusBinding
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.dialogs.form.ProcessoStatusAndamentoDialog
import com.example.advogo.models.ProcessoStatusAndamento
import com.example.advogo.repositories.IProcessoStatusAndamentoRepository
import com.example.advogo.utils.enums.UseAdapterBindingFor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoAndamentoStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoAndamentoStatusBinding
    private lateinit var bindingDialog: DialogListFormBinding
    @Inject lateinit var repository: IProcessoStatusAndamentoRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoAndamentoStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Andamentos Status", binding.toolbarProcessoAndamentoStatus)

        binding.fabAddProcessoAndamentoStatus.setOnClickListener {
            openDialog(null)
        }

        obterProcessoAndamentosStatus()
    }

    private fun setStatusToUI(lista: List<ProcessoStatusAndamento>) {
        if(lista.isNotEmpty()) {
            binding.rvStatusList.visibility = View.VISIBLE
            binding.tvNoStatusAvailable.visibility = View.GONE

            binding.rvStatusList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvStatusList.setHasFixedSize(true)

            val adapter = ProcessosStatusAndamentosAdapter(
                this,
                lista,
                UseAdapterBindingFor.ACTIVITY_OR_FRAGMENT
            )
            binding.rvStatusList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosStatusAndamentosAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoStatusAndamento, position: Int, action: String) {
                    openDialog(item)
                }

                override fun onEdit(item: ProcessoStatusAndamento, position: Int) {
                    openDialog(item)
                }

                override fun onDelete(item: ProcessoStatusAndamento, position: Int) {
                    alertDialogDeletar(item)
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

    private fun obterProcessoAndamentosStatus() {
        repository.obterProcessoStatusAndamentos(
            { lista ->
                setStatusToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun openDialog(status: ProcessoStatusAndamento? = null) {
        bindingDialog = DialogListFormBinding.inflate(layoutInflater)

        val dialog = object : ProcessoStatusAndamentoDialog(
            this,
            status ?: ProcessoStatusAndamento(),
            bindingDialog
        ) {
            override fun onSubmit(processoStatusAndamento: ProcessoStatusAndamento) {
                if(processoStatusAndamento.id.isBlank()) {
                    adicionar(processoStatusAndamento)
                } else {
                    atualizar(processoStatusAndamento)
                }
            }
        }

        dialog.show()
    }

    private fun adicionar(status: ProcessoStatusAndamento) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoStatusAndamento(
                id = "",
                status = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.adicionarProcessoStatusAndamento(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun validarFormulario(): Boolean {
        return true
    }

    private fun atualizar(status: ProcessoStatusAndamento) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoStatusAndamento(
                id = status.id,
                status = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.atualizarProcessoStatusAndamento(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun deletar(item: ProcessoStatusAndamento) {
        repository.deletarProcessoStatusAndamento (
            item.id,
            { deletarSuccess() },
            { deletarFailure() }
        )
    }

    private fun alertDialogDeletar(item: ProcessoStatusAndamento) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarStatus,
                item.status
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
        obterProcessoAndamentosStatus()
    }

    private fun deletarFailure() {
        hideProgressDialog()
    }

    private fun saveSuccess() {
        obterProcessoAndamentosStatus()
    }

    private fun saveFailure() {
        hideProgressDialog()
    }
}