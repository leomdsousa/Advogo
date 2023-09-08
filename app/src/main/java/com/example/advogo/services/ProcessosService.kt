package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import androidx.viewbinding.ViewBinding
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.repositories.IProcessoRepository
import javax.inject.Inject

class ProcessosService : Service() {
    @Inject lateinit var repository: IProcessoRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun validarProcessoSubmit(binding: ActivityProcessoCadastroBinding): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etProcessoName.text.toString())) {
            binding.etProcessoName.error = "Obrigatório"
            binding.etProcessoName.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etNumeroProcesso.text.toString())) {
            binding.etNumeroProcesso.error = "Obrigatório"
            binding.etNumeroProcesso.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDescricao.text.toString())) {
            binding.etDescricao.error = "Obrigatório"
            validado = false
        }

        if (TextUtils.isEmpty(binding.etTipoProcesso.text.toString())) {
            binding.etTipoProcesso.error = "Obrigatório"
            binding.etTipoProcesso.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etStatusProcesso.text.toString())) {
            binding.etStatusProcesso.error = "Obrigatório"
            binding.etStatusProcesso.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etData.text.toString())) {
            binding.etData.error = "Obrigatório"
            binding.etData.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etCliente.text.toString())) {
            binding.etCliente.error = "Obrigatório"
            binding.etCliente.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etAdv.text.toString())) {
            binding.etAdv.error = "Obrigatório"
            binding.etAdv.requestFocus()
            validado = false
        }

        return validado
    }
}