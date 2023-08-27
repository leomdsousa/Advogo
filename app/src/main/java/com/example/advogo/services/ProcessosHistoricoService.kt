package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import androidx.viewbinding.ViewBinding
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.databinding.DialogProcessoAndamentoBinding
import com.example.advogo.databinding.DialogProcessoAnexoBinding
import com.example.advogo.databinding.DialogProcessoHistoricoBinding
import com.example.advogo.repositories.IProcessoRepository
import javax.inject.Inject

class ProcessosHistoricoService : Service() {
    @Inject lateinit var repository: IProcessoRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun validarProcessoHistoricoSubmit(bindingDialog: DialogProcessoHistoricoBinding): Boolean {
        var validado = true

        if (TextUtils.isEmpty(bindingDialog.etDescricaoHistorico.text.toString())) {
            bindingDialog.etDescricaoHistorico.error = "Obrigatório"
            bindingDialog.etDescricaoHistorico.requestFocus()
            validado = false
        }

//        if (TextUtils.isEmpty(bindingDialog.etDataAndamento.text.toString())) {
//            bindingDialog.etDataAndamento.error = "Obrigatório"
//            bindingDialog.etDataAndamento.requestFocus()
//            validado = false
//        }

        return validado
    }
}