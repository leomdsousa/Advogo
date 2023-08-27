package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.example.advogo.databinding.ActivityDiligenciaCadastroBinding
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.databinding.DialogDiligenciaHistoricoBinding
import com.example.advogo.databinding.FragmentDiligenciaHistoricoBinding
import com.example.advogo.repositories.IDiligenciaRepository
import javax.inject.Inject

class DiligenciasHistoricoService : Service() {
    @Inject lateinit var repository: IDiligenciaRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun validarDiligenciaHistoricoSubmit(bindingDialog: DialogDiligenciaHistoricoBinding): Boolean {
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