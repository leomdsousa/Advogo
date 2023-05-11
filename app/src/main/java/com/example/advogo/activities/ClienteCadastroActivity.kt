package com.example.advogo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.advogo.R
import com.example.advogo.databinding.ActivityClienteCadastroBinding
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.services.CorreioApiService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteCadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClienteCadastroBinding
    @Inject lateinit var correioService: CorreioApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_cadastro)
    }

    private fun BuscarEnderecoCorreio(cep: String): CorreioResponse? {
        return correioService.obterEndereco(cep)
    }
}