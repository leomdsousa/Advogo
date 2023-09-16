package com.example.advogo.activities

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.databinding.ActivityDiligenciaStatusBinding
import com.example.advogo.databinding.DialogListFormBinding
import com.example.advogo.dialogs.form.DiligenciaStatusDialog
import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.repositories.IDiligenciaStatusRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DiligenciaStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaStatusBinding
    private lateinit var bindingDialog: DialogListFormBinding
    @Inject lateinit var repository: IDiligenciaStatusRepository

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiligenciaStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Diligencias Status", binding.toolbarDiligenciaStatus)

        binding.fabAddDiligenciaStatus.setOnClickListener {
            openDialog(null)
        }

        obterDiligenciaStatus()
    }

    private fun setStatusToUI(lista: List<DiligenciaStatus>) {
        if(lista.isNotEmpty()) {
            binding.rvStatusList.visibility = View.VISIBLE
            binding.tvNoStatusAvailable.visibility = View.GONE

            binding.rvStatusList.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvStatusList.setHasFixedSize(true)

            val adapter = DiligenciasStatusAdapter(
                this,
                lista,
                false
            )
            binding.rvStatusList.adapter = adapter

            adapter.setOnItemClickListener(object :
                DiligenciasStatusAdapter.OnItemClickListener {
                override fun onClick(item: DiligenciaStatus, position: Int, action: String) {
                    openDialog(item)
                }
                override fun onEdit(item: DiligenciaStatus, position: Int) {
                    openDialog(item)
                }
                override fun onDelete(item: DiligenciaStatus, position: Int) {
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

    private fun obterDiligenciaStatus() {
        repository.obterDiligenciasStatus(
            { lista ->
                setStatusToUI(lista)
                hideProgressDialog()
            },
            { hideProgressDialog() }
        )
    }

    private fun openDialog(diligenciaStatus: DiligenciaStatus? = null) {
        bindingDialog = DialogListFormBinding.inflate(layoutInflater)

        val dialog = object : DiligenciaStatusDialog(
            this,
            diligenciaStatus ?: DiligenciaStatus(),
            bindingDialog
        ) {
            override fun onSubmit(diligenciaStatus: DiligenciaStatus) {
                if(diligenciaStatus.id.isBlank()) {
                    adicionar(diligenciaStatus)
                } else {
                    atualizar(diligenciaStatus)
                }
            }
        }

        dialog.show()
    }

    private fun adicionar(diligenciaStatus: DiligenciaStatus) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = DiligenciaStatus(
                id = "",
                status = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.adicionarDiligenciaStatus(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun validarFormulario(): Boolean {
        return true
    }

    private fun atualizar(diligenciaStatus: DiligenciaStatus) {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val input = DiligenciaStatus(
                id = diligenciaStatus.id,
                status = bindingDialog.etDescription.text.toString(),
                ativo = true
            )

            repository.atualizarDiligenciaStatus(
                input,
                { saveSuccess() },
                { saveFailure() }
            )
        }
    }

    private fun deletar(item: DiligenciaStatus) {
        repository.deletarDiligenciaStatus(
            item.id,
            { deletarSuccess() },
            { deletarFailure() }
        )
    }

    private fun alertDialogDeletar(item: DiligenciaStatus) {
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
        obterDiligenciaStatus()
    }

    private fun deletarFailure() {
        hideProgressDialog()
    }

    private fun saveSuccess() {
        obterDiligenciaStatus()
    }

    private fun saveFailure() {
        hideProgressDialog()
    }
}