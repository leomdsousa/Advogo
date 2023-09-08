package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.example.advogo.databinding.ActivityDiligenciaCadastroBinding
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.repositories.IDiligenciaRepository
import javax.inject.Inject

class DiligenciasService : Service() {
    @Inject lateinit var repository: IDiligenciaRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun validarDiligenciaSubmit(binding: ActivityDiligenciaCadastroBinding): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etDiligenciaDescricao.text.toString())) {
            binding.etDiligenciaDescricao.error = "Obrigatório"
            binding.etDiligenciaDescricao.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDiligenciaData.text.toString())) {
            binding.etDiligenciaData.error = "Obrigatório"
            binding.etDiligenciaData.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDiligenciaEndereco.text.toString())) {
            binding.etDiligenciaEndereco.error = "Obrigatório"
            binding.etDiligenciaEndereco.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etTipoDiligencia.text.toString())) {
            binding.etTipoDiligencia.error = "Obrigatório"
            binding.etTipoDiligencia.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etStatusDiligencia.text.toString())) {
            binding.etStatusDiligencia.error = "Obrigatório"
            binding.etStatusDiligencia.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDiligenciaData.text.toString())) {
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDiligenciaAdvogado.text.toString())) {
            binding.etDiligenciaAdvogado.error = "Obrigatório"
            binding.etDiligenciaAdvogado.requestFocus()
            validado = false
        }

        return validado
    }
}