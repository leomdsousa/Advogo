package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import androidx.viewbinding.ViewBinding
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.databinding.DialogProcessoAndamentoBinding
import com.example.advogo.repositories.IProcessoRepository
import javax.inject.Inject

class ProcessosAndamentoService : Service() {
    @Inject lateinit var repository: IProcessoRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun validarProcessoAndamentoSubmit(bindingDialog: DialogProcessoAndamentoBinding): Boolean {
        var validado = true

        if (TextUtils.isEmpty(bindingDialog.etDescricaoAndamento .text.toString())) {
            bindingDialog.etDescricaoAndamento.error = "Obrigatório"
            bindingDialog.etDescricaoAndamento.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(bindingDialog.etDataAndamento.text.toString())) {
            bindingDialog.etDataAndamento.error = "Obrigatório"
            bindingDialog.etDataAndamento.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(bindingDialog.etTipoAndamentoProcesso.text.toString())) {
            bindingDialog.etTipoAndamentoProcesso.error = "Obrigatório"
            bindingDialog.etTipoAndamentoProcesso.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(bindingDialog.etStatusAndamentoProcesso.text.toString())) {
            bindingDialog.etStatusAndamentoProcesso.error = "Obrigatório"
            bindingDialog.etStatusAndamentoProcesso.requestFocus()
            validado = false
        }

//        if (bindingDialog.spinnerTipoAndamentoProcesso.selectedItem == null) {
//            validado = false
//        }
//
//        if (bindingDialog.spinnerStatusProcessoAndamento.selectedItem == null) {
//            validado = false
//        }

        return validado
    }
}