package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosStatusAdapter
import com.example.advogo.databinding.ActivityProcessoStatusBinding
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.dialogs.form.ProcessoStatusDialog
import com.example.advogo.models.ProcessoStatus
import com.example.advogo.repositories.IProcessoStatusRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoStatusBinding
    private lateinit var bindingDialog: DialogListFormBinding
    @Inject lateinit var repository: IProcessoStatusRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProcessoStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Processos Status", binding.toolbarProcessoStatus)

        binding.fabAddProcessoStatus.setOnClickListener {
            openDialog(null)
        }

        obterProcessosStatus()
    }

    private fun setStatusToUI(lista: List<ProcessoStatus>) {
        if(lista.isNotEmpty()) {
            binding.rvStatusList.visibility = View.VISIBLE
            binding.tvNoStatusAvailable.visibility = View.GONE

            binding.rvStatusList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvStatusList.setHasFixedSize(true)

            val adapter = ProcessosStatusAdapter(
                this,
                lista,
                false
            )
            binding.rvStatusList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosStatusAdapter.OnItemClickListener {
                override fun onClick(item: ProcessoStatus, position: Int, action: String) {
                    openDialog(item)
                }
                override fun onEdit(item: ProcessoStatus, position: Int) {
                    openDialog(item)
                }
                override fun onDelete(item: ProcessoStatus, position: Int) {
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

    private fun obterProcessosStatus() {
        repository.obterProcessosStatus(
            { lista ->
                setStatusToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun openDialog(processoStatus: ProcessoStatus? = null) {
        bindingDialog = DialogListFormBinding.inflate(layoutInflater)

        val dialog = object : ProcessoStatusDialog(
            this,
            processoStatus ?: ProcessoStatus(),
            bindingDialog
        ) {
            override fun onSubmit(processoStatus: ProcessoStatus) {
                if(processoStatus.id.isBlank()) {
                    adicionar(processoStatus)
                } else {
                    atualizar(processoStatus)
                }
            }
        }

        dialog.show()
    }

    private fun adicionar(processoStatus: ProcessoStatus) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoStatus(
                id = "",
                status = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.adicionarProcessoStatus(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun validarFormulario(): Boolean {
        return true
    }

    private fun atualizar(processoStatus: ProcessoStatus) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = ProcessoStatus(
                id = processoStatus.id,
                status = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.atualizarProcessoStatus(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun deletar(item: ProcessoStatus) {
        repository.deletarProcessoStatus(
            item.id,
            { deletarSuccess() },
            { deletarFailure() }
        )
    }

    private fun alertDialogDeletar(item: ProcessoStatus) {
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
        obterProcessosStatus()
    }

    private fun deletarFailure() {
        hideProgressDialog()
    }

    private fun saveSuccess() {
        obterProcessosStatus()
    }

    private fun saveFailure() {
        hideProgressDialog()
    }
}