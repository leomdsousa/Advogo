package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.example.advogo.databinding.ActivityClienteCadastroBinding
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.repositories.IClienteRepository
import javax.inject.Inject

class ClientesService : Service() {
    @Inject lateinit var repository: IClienteRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun validarClienteSubmit(binding: ActivityClienteCadastroBinding): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etNome.text.toString())) {
            binding.etNome.error = "Obrigatório"
            binding.etNome.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etCpf.text.toString())) {
            binding.etCpf.error = "Obrigatório"
            binding.etCpf.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEmail.text.toString())) {
            binding.etEmail.error = "Obrigatório"
            binding.etEmail.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etTelefone.text.toString())) {
            binding.etTelefone.error = "Obrigatório"
            binding.etTelefone.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEnderecoRua.text.toString())) {
            binding.etEnderecoRua.error = "Obrigatório"
            binding.etEnderecoRua.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEnderecoNumero.text.toString())) {
            binding.etEnderecoNumero.error = "Obrigatório"
            binding.etEnderecoNumero.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etBairro.text.toString())) {
            binding.etBairro.error = "Obrigatório"
            binding.etBairro.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEnderecoCidade.text.toString())) {
            binding.etEnderecoCidade.error = "Obrigatório"
            binding.etEnderecoCidade.requestFocus()
            validado = false
        }

        return validado
    }
}