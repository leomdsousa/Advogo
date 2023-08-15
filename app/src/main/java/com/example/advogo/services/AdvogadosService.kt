package com.example.advogo.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import com.example.advogo.activities.AdvogadoDetalheActivity
import com.example.advogo.activities.ClienteCadastroActivity
import com.example.advogo.databinding.ActivityAdvogadoDetalheBinding
import com.example.advogo.repositories.IAdvogadoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdvogadosService : Service() {
    @Inject lateinit var repository: IAdvogadoRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

//    fun validarFormulario(context: Context): Boolean {
//        var validado = true
//
//        when(context) {
//            is AdvogadoDetalheActivity -> {
//                if (TextUtils.isEmpty(binding.etName.text.toString())) {
//                    binding.etName.error = "Obrigatório"
//                    binding.etName.requestFocus()
//                    validado = false
//                }
//
//                if (TextUtils.isEmpty(binding.etSobrenome.text.toString())) {
//                    binding.etSobrenome.error = "Obrigatório"
//                    binding.etSobrenome.requestFocus()
//                    validado = false
//                }
//
//                if (TextUtils.isEmpty(binding.etEmail.text.toString())) {
//                    binding.etEmail.error = "Obrigatório"
//                    binding.etEmail.requestFocus()
//                    validado = false
//                }
//
//                if (TextUtils.isEmpty(binding.etTelefone.text.toString())) {
//                    binding.etTelefone.error = "Obrigatório"
//                    binding.etTelefone.requestFocus()
//                    validado = false
//                }
//
//                if (TextUtils.isEmpty(binding.etOab.text.toString())) {
//                    binding.etOab.error = "Obrigatório"
//                    binding.etOab.requestFocus()
//                    validado = false
//                }
//
//                if (TextUtils.isEmpty(binding.etEndereco.text.toString())) {
//                    binding.etEndereco.error = "Obrigatório"
//                    binding.etEndereco.requestFocus()
//                    validado = false
//                }
//            }
//        }
//
//        return validado
//    }
}